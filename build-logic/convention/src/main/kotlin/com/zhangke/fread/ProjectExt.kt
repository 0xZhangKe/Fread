package com.zhangke.fread

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal val Project.compose
    get() = org.jetbrains.compose.ComposePlugin.Dependencies(this)

internal fun Project.libraryExtension(action: LibraryExtension.() -> Unit) =
    extensions.configure<LibraryExtension>(action)

internal fun Project.applicationExtension(action: ApplicationExtension.() -> Unit) =
    extensions.configure<ApplicationExtension>(action)

internal fun Project.libraryComponentsExtension(action: LibraryAndroidComponentsExtension.() -> Unit) =
    extensions.configure<LibraryAndroidComponentsExtension>(action)

internal fun Project.applicationComponentsExtension(action: ApplicationAndroidComponentsExtension.() -> Unit) =
    extensions.configure<ApplicationAndroidComponentsExtension>(action)

internal fun Project.kotlinCompile(action: KotlinJvmCompile.() -> Unit) =
    tasks.withType<KotlinCompile>().all { action(this) }

internal fun Project.kotlin(action: KotlinProjectExtension.() -> Unit) =
    extensions.configure<KotlinProjectExtension>(action)

internal fun Project.java(action: JavaPluginExtension.() -> Unit) =
    extensions.configure<JavaPluginExtension>(action)
