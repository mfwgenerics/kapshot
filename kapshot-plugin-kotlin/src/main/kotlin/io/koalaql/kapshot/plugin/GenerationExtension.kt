package io.koalaql.kapshot.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class GenerationExtension(
    private val messages: MessageCollector
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val addSourceToBlock = pluginContext
            .referenceFunctions(CallableId(
                packageName = FqName("io.koalaql.kapshot"),
                callableName = Name.identifier("addSourceToBlock")
            ))
            .first()

        moduleFragment.accept(DebugVisitor(messages), Unit)

        moduleFragment.transform(
            CaptureTransformer(
                pluginContext,
                addSourceToBlock
            ),
            null
        )
    }
}
