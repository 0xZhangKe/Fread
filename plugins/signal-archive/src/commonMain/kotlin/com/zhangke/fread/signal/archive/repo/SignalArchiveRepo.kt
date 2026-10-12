package com.zhangke.fread.signal.archive.repo

import com.zhangke.framework.architect.http.sharedHttpClient
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.signal.archive.model.SignalArchiveArticle
import com.zhangke.fread.signal.archive.model.SignalArchiveIndex
import com.zhangke.fread.signal.archive.model.SignalArchivePage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

class SignalArchiveRepo(
    private val httpClient: HttpClient = sharedHttpClient,
    private val json: Json = globalJson,
) {

    suspend fun getIndex(baseUrl: String): Result<SignalArchiveIndex> =
        get(baseUrl, "rendered.json")

    suspend fun getPage(baseUrl: String, pagePath: String): Result<SignalArchivePage> =
        get(baseUrl, pagePath)

    suspend fun getArticle(baseUrl: String, detailPath: String): Result<SignalArchiveArticle> =
        get(baseUrl, detailPath)

    private suspend inline fun <reified T> get(baseUrl: String, path: String): Result<T> {
        return try {
            val response = httpClient.get(resolvePath(baseUrl, path)) {
                // Preserve status codes even if an injected client enables expectSuccess.
                expectSuccess = false
            }
            if (response.status.value != 200) {
                return Result.failure(SignalArchiveHttpException(response.status.value))
            }
            // Raw static hosting may serve JSON as text/plain.
            Result.success(json.decodeFromString<T>(response.bodyAsText()))
        } catch (exception: CancellationException) {
            // Cancellation must propagate to stop the caller's coroutine.
            throw exception
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    internal fun resolvePath(baseUrl: String, path: String): String {
        val base = Url(baseUrl.trim())
        require(base.protocol == URLProtocol.HTTP || base.protocol == URLProtocol.HTTPS) {
            "Signal Archive requires an HTTP(S) data URL"
        }
        return base.toString().trimEnd('/') + "/" + path
    }
}

/** A non-200 response, including deployment-window 404s and rate-limit 429s. */
class SignalArchiveHttpException(val statusCode: Int) : Exception(
    "Signal Archive request failed with HTTP $statusCode",
)
