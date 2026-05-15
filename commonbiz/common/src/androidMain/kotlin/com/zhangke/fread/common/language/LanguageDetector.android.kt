package com.zhangke.fread.common.language

import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android implementation backed by Google ML Kit's on-device language ID model.
 *
 * Thresholds mirror bsky-social-app's `SuggestedLanguage`:
 *  - `identifyPossibleLanguages` returns all candidates with confidence above
 *    a minimum (default 0.01, which is comparable to bsky's 0.0002 floor for
 *    "the model is at all unsure")
 *  - We only return a result when exactly one candidate survives that filter
 *    and its confidence is ≥ 0.97.
 */
actual class LanguageDetector actual constructor() {

    private val identifier by lazy {
        LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder()
                .setConfidenceThreshold(MIN_CONFIDENCE)
                .build()
        )
    }

    actual suspend fun detect(text: String): String? =
        suspendCancellableCoroutine { cont ->
            identifier.identifyPossibleLanguages(text)
                .addOnSuccessListener { identified ->
                    val confident = identified.filter { it.languageTag != "und" }
                    val top = confident.singleOrNull()
                    cont.resume(
                        top?.takeIf { it.confidence >= ACCEPT_CONFIDENCE }?.languageTag
                    )
                }
                .addOnFailureListener { cont.resume(null) }
        }

    private companion object {
        /** Minimum confidence for the model to include a candidate at all. */
        const val MIN_CONFIDENCE = 0.01F

        /** Confidence threshold above which we surface a suggestion to the user. */
        const val ACCEPT_CONFIDENCE = 0.97F
    }
}
