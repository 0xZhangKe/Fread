package com.zhangke.fread.common.language

/**
 * On-device detector for the language of free-form text. Used by the post
 * composer to suggest a language switch when what the user is typing differs
 * from the currently selected post language.
 *
 * Android is backed by ML Kit's Language Identification model; iOS is a
 * no-op stub for now.
 */
expect class LanguageDetector() {

    /**
     * Returns a BCP-47 language tag (e.g. `"en"`, `"es"`) if the model is
     * confident the text is in a single identifiable language. Returns
     * `null` otherwise — the caller should treat that as "no suggestion".
     *
     * The thresholds mirror bsky-social-app's `SuggestedLanguage`:
     * the model must surface exactly one candidate with confidence ≥ 0.97.
     */
    suspend fun detect(text: String): String?
}
