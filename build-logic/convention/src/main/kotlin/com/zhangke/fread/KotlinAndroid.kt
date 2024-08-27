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
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

        compileOptions {
            // Up to Java 11 APIs are available through desugaring
            // https://developer.android.com/studio/write/java11-minimal-support-table
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    configureKotlin()
}

/**
 * Configure base Kotlin options for JVM (non-Android)
 */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        // Up to Java 11 APIs are available through desugaring
        // https://developer.android.com/studio/write/java11-minimal-support-table
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    configureKotlin()
}

/**
 * Configure base Kotlin options
 */
private fun Project.configureKotlin() {
    // Use withType to workaround https://youtrack.jetbrains.com/issue/KT-55947
//    tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java){
//        compilerOptions{
//            languageVersion.set(KotlinVersion.KOTLIN_2_0)
//            freeCompilerArgs.add("-Xcontext-receivers")
//            freeCompilerArgs.add("-XXLanguage:+ExplicitBackingFields")
//            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
//            freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
//            freeCompilerArgs.add("-opt-in=kotlinx.coroutines.FlowPreview")
//        }
//    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            // Set JVM target to 11
            jvmTarget = JavaVersion.VERSION_11.toString()
            languageVersion = KotlinVersion.KOTLIN_2_0.version
            val newFreeCompilerArgs = freeCompilerArgs.toMutableList()
            newFreeCompilerArgs.add("-Xcontext-receivers")
            newFreeCompilerArgs.add("-XXLanguage:+ExplicitBackingFields")
            newFreeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            newFreeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
            newFreeCompilerArgs.add("-opt-in=kotlinx.coroutines.FlowPreview")
            newFreeCompilerArgs.add("-opt-in=androidx.compose.foundation.ExperimentalFoundationApi")
            freeCompilerArgs = newFreeCompilerArgs
            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()
        }
    }
    extensions.configure<KotlinProjectExtension>("kotlin") {
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
        sourceSets.all {
            languageSettings {
                languageVersion = "2.0"
            }
        }
    }
    tasks.withType<ProcessResources>{
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    tasks.withType<Jar>{
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}
