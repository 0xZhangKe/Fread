package com.zhangke.fread.common.ai

import com.zhangke.framework.architect.http.createHttpClient
import com.zhangke.framework.architect.http.createHttpClientEngine
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.ai.model.LLMModelConfig
import com.zhangke.fread.common.alttext.resizeAndJpegBase64
import com.zhangke.fread.common.utils.PlatformUriHelper
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.time.Duration.Companion.minutes

class LLMClient(
    private val modelConfigRepo: LLMModelConfigsRepo,
    private val platformUriHelper: PlatformUriHelper,
) {

    private val httpClient: HttpClient by lazy {
        createHttpClient(
            json = globalJson,
            engine = createHttpClientEngine(),
        ) {
            install(HttpTimeout) {
                requestTimeoutMillis = requestTimeout.inWholeMilliseconds
                socketTimeoutMillis = requestTimeout.inWholeMilliseconds
                connectTimeoutMillis = requestTimeout.inWholeMilliseconds
            }
        }
    }

    suspend fun execute(
        prompt: String,
        imageUri: PlatformUri? = null,
    ): Result<LLMResponse> {
        return runCatching {
            val config = modelConfigRepo.getSelectedModelConfig() ?: error("LLM is not configured.")
            val apiKey = config.apiKey.trim().takeIf { it.isNotBlank() }
                ?: error("LLM API key is not configured.")
            val response =
                httpClient.post(config.provider.baseUrl.trimEnd('/') + "/chat/completions") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $apiKey")
                        set(HttpHeaders.ContentType, "application/json; charset=utf-8")
                    }
                    setBody(buildRequestBody(config, prompt, imageUri))
                }
            val rawBody = response.bodyAsText()
            if (!response.status.isSuccess()) {
                val message = runCatching {
                    globalJson.parseToJsonElement(rawBody)
                        .jsonObject["error"]
                        ?.jsonObject
                        ?.get("message")
                        ?.jsonPrimitive
                        ?.contentOrNull
                }.getOrNull()
                error(message ?: "LLM server returned ${response.status}.")
            }
            parseResponse(rawBody)
        }
    }

    private fun parseResponse(rawBody: String): LLMResponse {
        val parsed = globalJson.parseToJsonElement(rawBody).jsonObject
        val content = parsed["choices"]?.jsonArray
            ?.firstOrNull()
            ?.jsonObject
            ?.get("message")
            ?.jsonObject
            ?.get("content")
        val text = extractTextContent(content)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: error("LLM returned an empty response.")
        val tokens = parsed["usage"]?.jsonObject?.let(::extractTotalTokens) ?: 0
        return LLMResponse(
            text = text,
            tokens = tokens,
        )
    }

    private suspend fun buildRequestBody(
        modelConfig: LLMModelConfig,
        prompt: String,
        imageUri: PlatformUri?,
    ): JsonObject {
        val base64 = imageUri?.let {
            val imageBytes = withContext(Dispatchers.IO) {
                platformUriHelper.readBytes(it)
            } ?: error("Could not load image.")
            withContext(Dispatchers.IO) {
                resizeAndJpegBase64(imageBytes)
            }
        }
        return buildJsonObject {
            put("model", modelConfig.versionName)
            putJsonArray("messages") {
                addJsonObject {
                    put("role", "user")
                    if (base64 == null) {
                        put("content", prompt)
                    } else {
                        putJsonArray("content") {
                            addJsonObject {
                                put("type", "text")
                                put("text", prompt)
                            }
                            addJsonObject {
                                put("type", "image_url")
                                putJsonObject("image_url") {
                                    put("url", "data:image/jpeg;base64,$base64")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun extractTextContent(content: JsonElement?): String? {
        return when (content) {
            null -> null
            is JsonPrimitive -> content.contentOrNull
            is JsonArray -> content.asSequence()
                .mapNotNull { part ->
                    val obj = part as? JsonObject ?: return@mapNotNull null
                    val type = obj["type"]?.jsonPrimitive?.contentOrNull
                    if (type == "text" || type == null) {
                        obj["text"]?.jsonPrimitive?.contentOrNull
                    } else {
                        null
                    }
                }
                .filter { it.isNotBlank() }
                .joinToString("\n")
                .takeIf { it.isNotBlank() }

            else -> null
        }
    }

    private fun extractTotalTokens(usage: JsonObject): Int {
        return usage["total_tokens"]?.jsonPrimitive?.intOrNull
            ?: usage["totalTokens"]?.jsonPrimitive?.intOrNull
            ?: usage["completion_tokens"]?.jsonPrimitive?.intOrNull
            ?: usage["completionTokens"]?.jsonPrimitive?.intOrNull
            ?: 0
    }

    private companion object {
        private val requestTimeout = 2.minutes
    }
}

data class LLMResponse(
    val text: String,
    val tokens: Int,
)
