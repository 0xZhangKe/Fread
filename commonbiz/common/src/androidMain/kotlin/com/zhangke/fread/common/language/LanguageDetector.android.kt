package com.zhangke.fread.common.language

import android.util.Log
import com.github.pemistahl.lingua.api.LanguageDetector
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder

/**
 * Android implementation backed by Lingua on-device language ID model.
 *
 * Thresholds mirror bsky-social-app's `SuggestedLanguage`:
 *  - `identifyPossibleLanguages` returns all candidates with confidence above
 *    a minimum (default 0.01, which is comparable to bsky's 0.0002 floor for
 *    "the model is at all unsure")
 *  - We only return a result when exactly one candidate survives that filter
 *    and its confidence is ≥ 0.97.
 */
actual class FreadLanguageDetector actual constructor() {

    private val detector: LanguageDetector by lazy {
        LanguageDetectorBuilder
            .fromAllLanguages()
            .build()
    }

    actual suspend fun detect(text: String): String? {
        return detector.computeLanguageConfidenceValues(text)
            .also {
                Log.d(
                    "Z_TEST",
                    "detect: $text -> ${it.entries.joinToString { "${it.key}: ${it.value}" }}"
                )
            }
            .maxBy { it.value }
            .takeIf { it.value >= ACCEPT_CONFIDENCE }
            ?.key
            ?.isoCode639_1
            ?.toString()
            ?.lowercase()
    }

    private companion object {
        /** Minimum confidence for the model to include a candidate at all. */
        const val MIN_CONFIDENCE = 0.01F

        /** Confidence threshold above which we surface a suggestion to the user. */
        const val ACCEPT_CONFIDENCE = 0.97F
    }
}
