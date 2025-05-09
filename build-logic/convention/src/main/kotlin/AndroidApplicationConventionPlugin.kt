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

import com.zhangke.fread.applicationComponentsExtension
import com.zhangke.fread.applicationExtension
import com.zhangke.fread.configureKotlinAndroid
import com.zhangke.fread.configurePrintApksTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.serialization")
                if (gradle.extra["enableFirebaseModule"] == true) {
                    println("Find the Firebase configuration file, add the Firebase plugin.")
                    apply("com.google.gms.google-services")
                    apply("com.google.firebase.crashlytics")
                }
            }

            applicationExtension {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 35
            }
            applicationComponentsExtension {
                configurePrintApksTask(this)
            }
        }
    }
}
