package com.zhangke.fread.common.handler

import androidx.compose.runtime.staticCompositionLocalOf

expect class TextHandler {
    fun copyText(text: String)

    fun shareUrl(
        url: String,
        text: String,
    )
}

val LocalTextHandler = staticCompositionLocalOf<TextHandler> { error("No TextHandler provided") }
