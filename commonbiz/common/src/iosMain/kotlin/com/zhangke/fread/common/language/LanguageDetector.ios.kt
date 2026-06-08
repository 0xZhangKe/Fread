package com.zhangke.fread.common.language

/**
 * iOS stub. Could be backed by `NSLinguisticTagger` later; returns null for now
 * so callers behave as "no suggestion".
 */
actual class LanguageDetector actual constructor() {
    actual suspend fun detect(text: String): String? = null
}
