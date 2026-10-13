package com.zhangke.fread.common.language

/** On-device language detection. Unsupported platforms return null. */
expect class LanguageDetector() : AutoCloseable {

    /** Returns an ISO 639-1 language tag, or null when no confident match is available. */
    suspend fun detect(text: String): String?

    override fun close()
}
