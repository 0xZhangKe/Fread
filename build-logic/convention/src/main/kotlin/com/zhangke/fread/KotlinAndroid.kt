/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhangke.fread

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 34

        defaultConfig {
            minSdk = 23
        }
    }
    configureKotlin()
    configureJava()
}

/**
 * Configure base Kotlin options for JVM (non-Android)
 */
internal fun Project.configureKotlinJvm() {
    configureKotlin()
    configureJava()
}

/**
 * Configure Java options
 */
private fun Project.configureJava() {
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

/**
 * Configure base Kotlin options
 */
private fun Project.configureKotlin() {
    kotlinCompile {
        compilerOptions {
            languageVersion = KotlinVersion.KOTLIN_2_0
            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()
        }
    }
    kotlin {
        sourceSets.all {
            languageSettings {
                languageVersion = KotlinVersion.KOTLIN_2_0.version
                enableLanguageFeature("ContextReceivers")
                enableLanguageFeature("ExplicitBackingFields")
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlinx.coroutines.FlowPreview")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
            }
        }
        sourceSets.findByName("main")?.apply {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
            resources.srcDir("build/generated/ksp/main/resources")
        }
        sourceSets.findByName("debug")?.apply {
            kotlin.srcDir("build/generated/ksp/debug/kotlin")
            resources.srcDir("build/generated/ksp/debug/resources")
        }
        sourceSets.findByName("release")?.apply {
            kotlin.srcDir("build/generated/ksp/release/kotlin")
            resources.srcDir("build/generated/ksp/release/resources")
        }
    }
    tasks.withType<ProcessResources>{
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    tasks.withType<Jar>{
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}
