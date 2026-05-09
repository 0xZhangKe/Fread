package com.zhangke.fread.common.alttext

import com.zhangke.framework.architect.http.sharedHttpClient
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.utils.PlatformUriHelper
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

private const val REQUEST_TIMEOUT_MS = 120_000L

class AltTextGenerator(
    private val freadConfigManager: FreadConfigManager,
    private val platformUriHelper: PlatformUriHelper,
) {

    private val httpClient get() = sharedHttpClient

    suspend fun generate(imageUri: PlatformUri): Result<AltTextResult> {
        return runCatching {
            val apiKey = freadConfigManager.getAltTextApiKey().trim()
            val model = freadConfigManager.getAltTextModel().trim()
            if (apiKey.isEmpty() || model.isEmpty()) {
                throw AltTextException.NotConfigured
            }
            val baseUrl = freadConfigManager.getAltTextBaseUrl().trim().trimEnd('/')
            val prompt = freadConfigManager.getAltTextPrompt()
            val maxTokens = freadConfigManager.getAltTextMaxTokens()

            val bytes = withContext(Dispatchers.IO) {
                platformUriHelper.readBytes(imageUri)
            } ?: throw AltTextException.LoadImage

            val base64 = withContext(Dispatchers.IO) {
                runCatching { resizeAndJpegBase64(bytes) }
                    .getOrElse { throw AltTextException.LoadImage }
            }

            val requestBody = buildJsonObject {
                put("model", model)
                put("max_tokens", maxTokens)
                putJsonArray("messages") {
                    addJsonObject {
                        put("role", "user")
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

            val response = try {
                withTimeout(REQUEST_TIMEOUT_MS) {
                    httpClient.post("$baseUrl/chat/completions") {
                        headers {
                            append(HttpHeaders.Authorization, "Bearer $apiKey")
                            // Override the global JSON Content-Type with an explicit charset.
                            set(HttpHeaders.ContentType, "application/json; charset=utf-8")
                        }
                        setBody(requestBody)
                    }
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: AltTextException) {
                throw e
            } catch (_: Throwable) {
                throw AltTextException.Network
            }

            val rawBody = response.bodyAsText()
            if (!response.status.isSuccess()) {
                val message = runCatching {
                    val obj = globalJson.parseToJsonElement(rawBody).jsonObject
                    obj["error"]?.jsonObject?.get("message")?.jsonPrimitive?.contentOrNull
                }.getOrNull()
                throw if (message.isNullOrBlank()) {
                    AltTextException.Server(null)
                } else {
                    AltTextException.Server(message)
                }
            }

            val parsed = runCatching {
                globalJson.parseToJsonElement(rawBody).jsonObject
            }.getOrElse { throw AltTextException.Server(null) }

            val rawContent = parsed["choices"]?.jsonArray
                ?.firstOrNull()
                ?.jsonObject
                ?.get("message")
                ?.jsonObject
                ?.get("content")

            val text = extractTextContent(rawContent)
                ?.trim()
                ?.stripWrappingQuotes()

            if (text.isNullOrBlank()) throw AltTextException.EmptyResponse

            val provider = parsed["provider"]?.jsonPrimitive?.contentOrNull
            val cost = parsed["usage"]?.jsonObject?.get("cost")?.jsonPrimitive?.doubleOrNull

            AltTextResult(
                text = text,
                provider = provider?.takeIf { it.isNotBlank() },
                costUsd = cost,
            )
        }
    }

    private fun extractTextContent(content: kotlinx.serialization.json.JsonElement?): String? {
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

    private fun String.stripWrappingQuotes(): String {
        return if (length >= 2 && startsWith('"') && endsWith('"')) {
            substring(1, length - 1)
        } else {
            this
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
