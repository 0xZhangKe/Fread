package com.zhangke.fread.common.language

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import opennlp.tools.langdetect.LanguageDetectorME
import opennlp.tools.langdetect.LanguageDetectorModel
import java.util.Locale

actual class LanguageDetector actual constructor() : AutoCloseable {

    private val lock = Any()
    private var identifier: LanguageDetectorME? = null
    private var closed = false

    actual suspend fun detect(text: String): String? {
        if (text.isBlank() || text.none { it.isLetter() }) return null
        return withContext(Dispatchers.Default) {
            try {
                synchronized(lock) {
                    if (closed) return@synchronized null
                    val detector = identifier ?: loadDetector().also { identifier = it }
                    val prediction = detector.predictLanguage(text)
                    prediction?.lang?.let { iso6391Codes[it] }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun loadDetector(): LanguageDetectorME {
        val stream = checkNotNull(javaClass.getResourceAsStream("/opennlp/langdetect-183.bin")) {
            "OpenNLP language detection model is missing"
        }
        return stream.use { LanguageDetectorME(LanguageDetectorModel(it)) }
    }

    actual override fun close() {
        synchronized(lock) {
            closed = true
            identifier = null
        }
    }

    private companion object {

        val iso6391Codes: Map<String, String> by lazy {
            Locale.getISOLanguages().associateBy { Locale(it).isO3Language } + mapOf(
                // The model also emits individual languages within ISO 639 macrolanguages.
                "cmn" to "zh",
                "nan" to "zh",
                "ekk" to "et",
                "lvs" to "lv",
                "pes" to "fa",
                "plt" to "mg",
                "pnb" to "pa",
            )
        }
    }
}
