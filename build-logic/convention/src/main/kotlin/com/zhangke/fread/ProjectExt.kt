package com.zhangke.fread

import org.gradle.api.Project

internal val Project.compose
    get() = org.jetbrains.compose.ComposePlugin.Dependencies(this)