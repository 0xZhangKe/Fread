package com.zhangke.fread.common.handler

import androidx.compose.runtime.staticCompositionLocalOf

expect class TextHandler {

    val packageName: String

    val versionName: String

    val versionCode: String

    fun copyText(text: String)

    fun shareUrl(url: String, text: String)

    fun openSendEmail()

    fun openAppMarket()
}

val LocalTextHandler =
    staticCompositionLocalOf<TextHandler> { error("No TextHandler provided") }
