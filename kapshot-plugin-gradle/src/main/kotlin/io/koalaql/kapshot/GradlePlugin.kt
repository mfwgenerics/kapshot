package io.koalaql.kapshot

import io.koalaql.kapshot_plugin_gradle.BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class GradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun getCompilerPluginId(): String = "io.koalaql.kapshot-plugin"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "io.koalaql",
        artifactId = "kapshot-plugin-kotlin",
        version = BuildConfig.VERSION
    )

    override fun apply(target: Project) {
        /* make sure we don't try to add dependency until it has been configured by kotlin plugin */
        target.plugins.withId("org.jetbrains.kotlin.jvm") {
            target.dependencies.add("implementation", "io.koalaql:kapshot-runtime:${BuildConfig.VERSION}")
        }
    }

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project

        return project.provider {
          emptyList()
        }
    }
}
