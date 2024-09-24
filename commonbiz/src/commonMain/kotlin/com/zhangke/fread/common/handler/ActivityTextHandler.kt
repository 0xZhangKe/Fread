package com.zhangke.fread.common.handler

import androidx.compose.runtime.staticCompositionLocalOf

// TODO: rename TextHandler

expect class TextHandler {

    val packageName: String

    val versionName: String

    val versionCode: String
}

expect class ActivityTextHandler {

    val packageName: String

    val versionName: String

    val versionCode: String

    fun copyText(text: String)

    fun shareUrl(
        url: String,
        text: String,
    )

    fun openSendEmail()

    fun openAppMarket()
}

val LocalActivityTextHandler = staticCompositionLocalOf<ActivityTextHandler> { error("No TextHandler provided") }
