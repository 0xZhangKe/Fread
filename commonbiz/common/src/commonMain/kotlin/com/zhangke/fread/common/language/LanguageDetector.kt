package com.zhangke.fread.common.language

import com.zhangke.lingua.LanguageDetectorBuilder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * On-device language detection shared by Android and iOS using lingua-core.
 * Native detection is blocking, so initialization and detection run off the UI thread.
 */
class LanguageDetector : AutoCloseable {

    private val identifier by lazy {
        LanguageDetectorBuilder.fromAllLanguages().build()
    }

    /**
     * Returns an ISO 639-1 language tag (e.g. "en", "es"), or null for blank,
     * ambiguous text or detection failures. Lingua's relative confidence scores
     * must contain exactly one candidate at or above 0.01, with a score of at least 0.97.
     */
    suspend fun detect(text: String): String? {
        if (text.isBlank()) return null
        return withContext(Dispatchers.Default) {
            try {
                identifier.computeLanguageConfidenceValues(text)
                    .singleOrNull { it.value >= MIN_CONFIDENCE }
                    ?.takeIf { it.value >= ACCEPT_CONFIDENCE }
                    ?.language
                    ?.isoCode6391
                    ?.code
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                null
            }
        }
    }

    override fun close() {
        identifier.close()
    }

    private companion object {
        const val MIN_CONFIDENCE = 0.01
        const val ACCEPT_CONFIDENCE = 0.97
    }
}
