package com.zhangke.fread.common.language

actual class LanguageDetector actual constructor() : AutoCloseable {

    actual suspend fun detect(text: String): String? = null

    actual override fun close() = Unit
}
