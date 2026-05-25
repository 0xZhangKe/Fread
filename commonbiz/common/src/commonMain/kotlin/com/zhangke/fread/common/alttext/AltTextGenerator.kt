package com.zhangke.fread.common.alttext

import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.ai.LLMClient
import com.zhangke.fread.common.config.FreadConfigManager
import kotlinx.coroutines.CancellationException

class AltTextGenerator(
    private val llmClient: LLMClient,
    private val freadConfigManager: FreadConfigManager,
) {

    suspend fun generate(imageUri: PlatformUri): Result<AltTextResult> {
        return runCatching {
            val prompt = freadConfigManager.getAltTextPrompt()
            val response = llmClient.execute(prompt, imageUri)
                .getOrElse { throw it.toAltTextException() }
            val text = response.text.stripWrappingQuotes()

            if (text.isBlank()) throw AltTextException.EmptyResponse

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
            message.contains("not configured", ignoreCase = true) -> AltTextException.NotConfigured
            message.contains("load image", ignoreCase = true) -> AltTextException.LoadImage
            message.contains("empty response", ignoreCase = true) -> AltTextException.EmptyResponse
            message.isBlank() -> AltTextException.Network
            else -> AltTextException.Server(message)
        }
    }
}

sealed class AltTextException : Exception() {
    object NotConfigured : AltTextException()
    object LoadImage : AltTextException()
    class Server(val serverMessage: String?) : AltTextException()
    object Network : AltTextException()
    object EmptyResponse : AltTextException()
}
