package com.zhangke.framework.architect.http

import com.zhangke.framework.architect.json.globalJson
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val sharedHttpClient: HttpClient by lazy {
    createHttpClient(globalJson, createHttpClientEngine())
}

expect fun createHttpClientEngine(): HttpClientEngine

private fun createHttpClient(
    json: Json,
    engine: HttpClientEngine,
): HttpClient {
    return HttpClient(engine) {
        install(ContentNegotiation) {
            json(json)
        }
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }
}
