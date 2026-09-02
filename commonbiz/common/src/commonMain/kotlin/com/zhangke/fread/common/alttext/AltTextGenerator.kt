package com.zhangke.fread.common.alttext

import ai.koog.prompt.llm.LLMCapability
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.ai.LLMClient
import com.zhangke.fread.common.ai.LLMModelConfigsRepo
import com.zhangke.fread.common.ai.model.koogModels
import com.zhangke.fread.common.config.FreadConfigManager
import kotlinx.coroutines.CancellationException

class AltTextGenerator(
    private val llmClient: LLMClient,
    private val modelConfigRepo: LLMModelConfigsRepo,
    private val freadConfigManager: FreadConfigManager,
) {

    suspend fun available(): Boolean {
        val config = modelConfigRepo.getSelectedModelConfig() ?: return false
        val apiKeyAvailable = config.provider.id == "ollama" || config.apiKey.isNotBlank()
        val supportsImage = config.provider.koogModels
            .firstOrNull { it.id == config.versionName }
            ?.supports(LLMCapability.Vision.Image) == true
        return apiKeyAvailable && supportsImage
    }

    suspend fun generate(imageUri: PlatformUri): Result<AltTextResult> {
        return runCatching {
            val prompt = freadConfigManager.getAltTextPrompt()
            val response = llmClient.execute(prompt, imageUri)
                .getOrElse { throw it.toAltTextException() }
            val text = response.text
                .stripThinkingText()
                .stripWrappingQuotes()

            if (text.isBlank()) throw AltTextException.EmptyResponse()

            AltTextResult(
                text = text,
                provider = null,
                costUsd = null,
            )
        }
    }

    private fun String.stripWrappingQuotes(): String {
        return if (length >= 2 && startsWith('"') && endsWith('"')) {
            substring(1, length - 1)
        } else {
            this
        }
    }

    private fun Throwable.toAltTextException(): Throwable {
        if (this is CancellationException) return this
        val message = message.orEmpty()
        return when {
            message.contains("not configured", ignoreCase = true) -> AltTextException.NotConfigured()
            message.contains("load image", ignoreCase = true) -> AltTextException.LoadImage()
            message.contains("empty response", ignoreCase = true) -> AltTextException.EmptyResponse()
            message.isBlank() -> AltTextException.Network()
            else -> AltTextException.Server(message)
        }
    }
}

internal fun String.stripThinkingText(): String {
    val textAfterThinking = substringAfterLast(THINK_END_TAG, missingDelimiterValue = "")
        .trim()
    return textAfterThinking.ifBlank { this }
}

private const val THINK_END_TAG = "</think>"

sealed class AltTextException : Exception() {
    class NotConfigured : AltTextException()
    class LoadImage : AltTextException()
    class Server(val serverMessage: String?) : AltTextException()
    class Network : AltTextException()
    class EmptyResponse : AltTextException()
}
