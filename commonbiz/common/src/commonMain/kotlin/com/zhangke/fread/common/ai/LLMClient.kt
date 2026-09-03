package com.zhangke.fread.common.ai

import ai.koog.http.client.ktor.KtorKoogHttpClient
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.ConnectionTimeoutConfig
import ai.koog.prompt.executor.clients.LLMClient as KoogLLMClient
import ai.koog.prompt.executor.clients.anthropic.AnthropicClientSettings
import ai.koog.prompt.executor.clients.anthropic.AnthropicLLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.AttachmentContent
import ai.koog.prompt.message.AttachmentSource
import com.zhangke.framework.architect.http.createHttpClientEngine
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.ai.model.LLMModelConfig
import com.zhangke.fread.common.ai.model.LLMProvider
import com.zhangke.fread.common.ai.model.resolveKoogModel
import com.zhangke.fread.common.alttext.resizeAndJpegBase64
import com.zhangke.fread.common.utils.PlatformUriHelper
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.minutes

class LLMClient(
    private val modelConfigRepo: LLMModelConfigsRepo,
    private val platformUriHelper: PlatformUriHelper,
) {

    private val clients = mutableMapOf<ClientKey, KoogLLMClient>()
    private val clientsMutex = Mutex()

    private val httpClientFactory: KtorKoogHttpClient.Factory by lazy {
        KtorKoogHttpClient.Factory(
            baseClient = HttpClient(createHttpClientEngine()),
            withSse = false,
        )
    }

    suspend fun execute(
        prompt: String,
        imageUri: PlatformUri? = null,
    ): Result<LLMResponse> {
        return runCatching {
            val config = modelConfigRepo.getSelectedModelConfig()
                ?: error("LLM is not configured.")
            val model = config.resolveKoogModel()
            if (imageUri != null && !model.supports(LLMCapability.Vision.Image)) {
                error("Model ${model.id} does not support image input.")
            }

            val response = createKoogClient(config).execute(
                prompt = buildKoogPrompt(prompt, imageUri),
                model = model,
                tools = emptyList(),
            )
            val text = response.textContent().trim().takeIf(String::isNotBlank)
                ?: error("LLM returned an empty response.")
            LLMResponse(
                text = text,
                tokens = response.metaInfo.totalTokensCount ?: 0,
            )
        }
    }

    private suspend fun createKoogClient(config: LLMModelConfig): KoogLLMClient {
        val baseUrl = config.provider.normalizedBaseUrl
        val key = ClientKey(
            providerId = config.provider.id,
            baseUrl = baseUrl,
            apiKey = config.apiKey,
        )
        return clientsMutex.withLock {
            clients.getOrPut(key) { createKoogClient(config, baseUrl) }
        }
    }

    private fun createKoogClient(
        config: LLMModelConfig,
        baseUrl: String,
    ): KoogLLMClient {
        val timeoutConfig = ConnectionTimeoutConfig(
            requestTimeoutMillis = requestTimeout.inWholeMilliseconds,
            connectTimeoutMillis = requestTimeout.inWholeMilliseconds,
            socketTimeoutMillis = requestTimeout.inWholeMilliseconds,
        )
        return when (config.provider.id) {
            in openAICompatibleProviderIds -> OpenAILLMClient(
                apiKey = config.requireApiKey(),
                settings = OpenAIClientSettings(
                    baseUrl = baseUrl,
                    chatCompletionsPath = config.provider.chatCompletionsPath,
                    timeoutConfig = timeoutConfig,
                ),
                httpClientFactory = httpClientFactory,
            )

            "anthropic" -> AnthropicLLMClient(
                apiKey = config.requireApiKey(),
                settings = AnthropicClientSettings(
                    baseUrl = baseUrl,
                    timeoutConfig = timeoutConfig,
                ),
                httpClientFactory = httpClientFactory,
            )

            "ollama" -> OllamaClient(
                httpClientFactory = httpClientFactory,
                baseUrl = baseUrl,
                headers = config.apiKey.trim().takeIf(String::isNotBlank)
                    ?.let { mapOf("Authorization" to "Bearer $it") }
                    .orEmpty(),
                timeoutConfig = timeoutConfig,
            )

            else -> OpenAILLMClient(
                apiKey = config.requireApiKey(),
                settings = OpenAIClientSettings(
                    baseUrl = baseUrl,
                    chatCompletionsPath = "v1/chat/completions",
                    timeoutConfig = timeoutConfig,
                ),
                httpClientFactory = httpClientFactory,
            )
        }
    }

    private suspend fun buildKoogPrompt(
        promptText: String,
        imageUri: PlatformUri?,
    ) = imageUri?.let { uri ->
        val imageBytes = withContext(Dispatchers.IO) {
            platformUriHelper.readBytes(uri)
        } ?: error("Could not load image.")
        val imageBase64 = withContext(Dispatchers.IO) {
            resizeAndJpegBase64(imageBytes)
        }
        AttachmentSource.Image(
            content = AttachmentContent.Binary.Base64(imageBase64),
            format = "jpeg",
            mimeType = "image/jpeg",
        )
    }.let { imageAttachment ->
        prompt("fread-request") {
            if (imageAttachment == null) {
                user(promptText)
            } else {
                user {
                    text(promptText)
                    image(imageAttachment)
                }
            }
        }
    }

    private fun LLMModelConfig.resolveKoogModel(): LLModel {
        return provider.resolveKoogModel(versionName)
    }

    private fun LLMModelConfig.requireApiKey(): String {
        return apiKey.trim().takeIf(String::isNotBlank)
            ?: error("LLM API key is not configured.")
    }

    private val LLMProvider.normalizedBaseUrl: String
        get() {
            val normalized = baseUrl.trim().trimEnd('/').removeSuffix("/v1")
            return if (id == "google" && normalized.endsWith("/v1beta")) {
                "$normalized/openai"
            } else {
                normalized
            }
        }

    private val LLMProvider.chatCompletionsPath: String
        get() = when (id) {
            "google", "zhipuai" -> "chat/completions"
            else -> "v1/chat/completions"
        }

    private companion object {
        private val requestTimeout = 2.minutes
        private val openAICompatibleProviderIds = setOf(
            "openai",
            "google",
            "meta",
            "alibaba",
            "openrouter",
            "deepseek",
            "mistralai",
            "minimax",
            "zhipuai",
            "huggingface",
        )
    }

    private data class ClientKey(
        val providerId: String,
        val baseUrl: String,
        val apiKey: String,
    )
}

data class LLMResponse(
    val text: String,
    val tokens: Int,
)
