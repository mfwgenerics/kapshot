package io.koalaql.kapshot.plugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.sourceElement
import org.jetbrains.kotlin.backend.jvm.ir.getStringConstArgument
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
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

    private fun encodeSourceLocation(
        start: Int,
        end: Int
    ): String {
        val entry = currentFile.fileEntry
        val path = projectDir.relativize(Path(currentFile.path))

        fun encodeOffset(offset: Int): String =
            "$offset,${entry.getLineNumber(offset)},${entry.getColumnNumber(offset)}"

        return "$path\n${encodeOffset(start)}\n${encodeOffset(end)}"
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

            val trimmed = fileText.substring(startOffset, endOffset).trimIndent().trim()

            addSourceCall.putTypeArgument(0, expression.type)

            /* super call here rather than directly using expression is required to support nesting. otherwise we don't transform the subtree */
            addSourceCall.putValueArgument(0, super.visitTypeOperator(expression))
            addSourceCall.putValueArgument(1, irString(encodeSourceLocation(startOffset, endOffset)))
            addSourceCall.putValueArgument(2, irString(trimmed))

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
            val fileText = currentFileText()

            /* we start from end of captureSource rather than declaration.startOffset to exclude the capture annotation */
            val startOffset = captureSource.endOffset
            val endOffset = declaration.endOffset

            val trimmed = fileText.substring(startOffset, endOffset).trimIndent().trim()

            captureSource.putValueArgument(0,
                IrConstImpl.string(captureSource.startOffset, captureSource.endOffset, context.irBuiltIns.stringType, trimmed)
            )
        }

        return super.visitDeclaration(declaration)
    }
}
