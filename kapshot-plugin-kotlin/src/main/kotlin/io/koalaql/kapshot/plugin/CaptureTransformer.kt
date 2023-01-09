package io.koalaql.kapshot.plugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.sourceElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.kotlinFqName
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

class CaptureTransformer(
    private val context: IrPluginContext,
    private val projectDir: Path,
    private val addSourceToBlock: IrSimpleFunctionSymbol,
    private val capturableFqn: String = "io.koalaql.kapshot.Capturable",
    private val captureSourceFqn: String = "io.koalaql.kapshot.CaptureSource",
): IrElementTransformerVoidWithContext() {
    fun currentFileText(): String {
        /* https://youtrack.jetbrains.com/issue/KT-41888 */
        return File(currentFile.path).readText().replace("\r\n", "\n")
    }

    private class Source(
        val text: String,
        val location: String
    )

    private fun extractSource(
        fileText: String,
        start: Int,
        end: Int,
    ): Source {
        val entry = currentFile.fileEntry
        val path = projectDir.relativize(Path(currentFile.path))

        /* trim offsets so source location info matches .trimIndent().trim(). TODO roll trimIndent().trim() ourselves */
        var trimmedStart = start
        var trimmedEnd = end

        while (trimmedEnd > trimmedStart && fileText[trimmedEnd - 1].isWhitespace()) trimmedEnd--
        while (trimmedStart < trimmedEnd && fileText[trimmedStart].isWhitespace()) trimmedStart++

        fun encodeOffset(offset: Int): String =
            "$offset,${entry.getLineNumber(offset)},${entry.getColumnNumber(offset)}"

        return Source(
            text = fileText.substring(start, end).trimIndent().trim(),
            location = "$path\n${encodeOffset(trimmedStart)}\n${encodeOffset(trimmedEnd)}"
        )
    }

    private fun transformSam(expression: IrTypeOperatorCall): IrExpression {
        val symbol = currentScope!!.scope.scopeOwnerSymbol

        val sourceElement = expression.sourceElement() ?: return super.visitTypeOperator(expression)

        return with(DeclarationIrBuilder(context, symbol, expression.startOffset, expression.endOffset)) {
            val addSourceCall = this.irCall(
                addSourceToBlock,
                expression.typeOperand
            )

            val fileText = currentFileText()

            var startOffset = sourceElement.startOffset
            var endOffset = sourceElement.endOffset

            while (endOffset > startOffset && fileText[endOffset - 1] != '}') endOffset--
            while (startOffset < endOffset && fileText[startOffset] != '{') startOffset++

            if (endOffset > startOffset + 1) {
                /* assume {...} and trim */
                endOffset--
                startOffset++
            }

            val source = extractSource(fileText, startOffset, endOffset)

            addSourceCall.putTypeArgument(0, expression.type)

            /* super call here rather than directly using expression is required to support nesting. otherwise we don't transform the subtree */
            addSourceCall.putValueArgument(0, super.visitTypeOperator(expression))
            addSourceCall.putValueArgument(1, irString(source.location))
            addSourceCall.putValueArgument(2, irString(source.text))

            addSourceCall
        }
    }

    private fun typeIsFqn(type: IrType, fqn: String): Boolean {
        if (type !is IrSimpleType) return false

        return when (val owner = type.classifier.owner) {
            is IrClass -> owner.kotlinFqName.asString() == fqn
            else -> false
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall): IrExpression {
        if (expression.operator == IrTypeOperator.SAM_CONVERSION) {
            when (val type = expression.type) {
                is IrSimpleType -> {
                    when (val owner = type.classifier.owner) {
                        is IrClass -> if (owner.superTypes.any { typeIsFqn(it, capturableFqn) }) {
                            return transformSam(expression)
                        }
                    }
                }
            }
        }

        return super.visitTypeOperator(expression)
    }

    override fun visitDeclaration(declaration: IrDeclarationBase): IrStatement {
        val captureSource = declaration.annotations.singleOrNull {
            typeIsFqn(it.type, captureSourceFqn)
        }

        if (captureSource != null) {
            val source = extractSource(
                currentFileText(),
                /* we start from end of captureSource rather than declaration.startOffset to exclude the capture annotation */
                captureSource.endOffset,
                declaration.endOffset
            )

            captureSource.putValueArgument(0,
                IrConstImpl.string(
                    captureSource.startOffset,
                    captureSource.endOffset,
                    context.irBuiltIns.stringType,
                    source.location
                )
            )

            captureSource.putValueArgument(1,
                IrConstImpl.string(
                    captureSource.startOffset,
                    captureSource.endOffset,
                    context.irBuiltIns.stringType,
                    source.text
                )
            )
        }

        return super.visitDeclaration(declaration)
    }
}
