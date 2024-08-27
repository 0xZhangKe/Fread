/*
 * Copyright 2022 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposeMultiPlatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
            }
            if (pluginManager.hasPlugin("com.android.application")) {
                applicationExtension {
                    buildFeatures {
                        compose = true
                    }
                }
            } else if (pluginManager.hasPlugin("com.android.library")) {
                libraryExtension {
                    buildFeatures {
                        compose = true
                    }
                }
            }
            composeCompiler {
                // Enable 'strong skipping'
                // https://medium.com/androiddevelopers/jetpack-compose-strong-skipping-mode-explained-cbdb2aa4b900
                enableStrongSkippingMode.set(true)

                if (project.providers.gradleProperty("myapp.enableComposeCompilerReports").isPresent) {
                    val composeReports = layout.buildDirectory.map { it.dir("reports").dir("compose") }
                    reportsDestination.set(composeReports)
                    metricsDestination.set(composeReports)
                }
            }
        }
    }

    private fun Project.libraryExtension(action: LibraryExtension.() -> Unit) =
        extensions.configure<LibraryExtension>(action)

    private fun Project.applicationExtension(action: ApplicationExtension.() -> Unit) =
        extensions.configure<ApplicationExtension>(action)

    private fun Project.composeCompiler(block: ComposeCompilerGradlePluginExtension.() -> Unit) {
        extensions.configure<ComposeCompilerGradlePluginExtension>(block)
    }
}
