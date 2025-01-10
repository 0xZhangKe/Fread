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

import com.zhangke.fread.configureKotlinAndroid
import com.zhangke.fread.configurePrintApksTask
import com.zhangke.fread.kotlinMultiplatform
import com.zhangke.fread.libraryComponentsExtension
import com.zhangke.fread.libraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

class KotlinMultiplatformLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }
            libraryExtension {
                configureKotlinAndroid(this)
            }
            libraryComponentsExtension {
                configurePrintApksTask(this)
            }
            kotlinMultiplatform {
                androidTarget()
                iosX64()
                iosArm64()
                iosSimulatorArm64()
                @OptIn(ExperimentalKotlinGradlePluginApi::class)
                applyHierarchyTemplate {
                    common {
                        withAndroidTarget()
                        group("ios") {
                            withIosX64()
                            withIosArm64()
                            withIosSimulatorArm64()
                        }
                    }
                }
                targets.configureEach {
                    compilations.configureEach {
                        compileTaskProvider.configure {
                            compilerOptions {
                                // https://youtrack.jetbrains.com/issue/KT-61573
                                freeCompilerArgs.add("-Xexpect-actual-classes")
                            }
                        }
                    }
                }
            }
        }
    }
}