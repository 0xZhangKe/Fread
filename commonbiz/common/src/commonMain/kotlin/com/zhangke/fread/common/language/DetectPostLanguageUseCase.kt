package com.zhangke.fread.common.language

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class DetectPostLanguageUseCase(
    private val languageDetector: LanguageDetector,
) {

    /**
     * Mirrors bsky-social-app's `SuggestedLanguage` heuristic: only attempt
     * detection once the user has typed enough text. The [LanguageDetector]
     * applies the stricter "single confident match" thresholds.
     */
    suspend operator fun invoke(
        text: String,
        selectedLanguages: Collection<String>,
        dismissedLanguages: Collection<String>,
    ): String? {
        val trimmed = text.trim()
        if (trimmed.length < MIN_DETECT_LENGTH) return null
        delay(DETECT_DEBOUNCE_MS.milliseconds)
        val detected = languageDetector.detect(trimmed)?.normalizeLanguageTag() ?: return null
        val normalizedSelectedLanguages = selectedLanguages.map { it.normalizeLanguageTag() }.toSet()
        val normalizedDismissedLanguages = dismissedLanguages.map { it.normalizeLanguageTag() }.toSet()
        return detected
            .takeIf { it !in normalizedSelectedLanguages }
            ?.takeIf { it !in normalizedDismissedLanguages }
    }

    private fun String.normalizeLanguageTag(): String {
        return lowercase().substringBefore('-')
    }

    private companion object {
        const val MIN_DETECT_LENGTH = 40
        const val DETECT_DEBOUNCE_MS = 350L
    }
}
