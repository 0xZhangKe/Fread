package com.zhangke.fread.bluesky.internal.client

import com.zhangke.framework.network.FormalBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class BlueskyClient(
    val baseUrl: FormalBaseUrl,
    private val engine: HttpClientEngine,
    val json: Json,
) {

    private val blueskyService by lazy { BlueskyService(createHttpClient()) }

    private fun createHttpClient(): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(json)
            }
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }
}
