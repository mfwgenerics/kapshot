package io.koalaql.kapshot.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.name.FqName

class GenerationExtension(
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val addSourceToBlock = pluginContext.referenceFunctions(FqName("io.koalaql.kapshot.addSourceToBlock"))
            .first()

        moduleFragment.transform(
            CaptureTransformer(
                pluginContext,
                addSourceToBlock
            ),
            null
        )
    }
}
