package com.zhangke.fread.bluesky.internal.client

import com.zhangke.framework.network.FormalBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.http.Url
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import sh.christian.ozone.BlueskyApi
import sh.christian.ozone.XrpcBlueskyApi

class BlueskyClient(
    val baseUrl: FormalBaseUrl,
    private val engine: HttpClientEngine,
    val json: Json,
) : BlueskyApi by XrpcBlueskyApi(createBlueskyHttpClient(engine, json, baseUrl.toString())) {

    init {

    }
}

private fun createBlueskyHttpClient(
    engine: HttpClientEngine,
    json: Json,
    baseUrl: String,
): HttpClient {
    return HttpClient(engine) {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(DefaultRequest) {
            val hostUrl = Url(baseUrl)
            url.protocol = hostUrl.protocol
            url.host = hostUrl.host
            url.port = hostUrl.port
        }
//        install(XrpcAuthPlugin) {
//            json = this.json
//            this.accountKey = accountKey
//            this.accountQueries = accountQueries
//        }
        install(AtProtoProxyPlugin)

        expectSuccess = false

    }
}

private class AtProtoProxyPlugin {
    companion object : HttpClientPlugin<Unit, AtProtoProxyPlugin> {
        override val key = AttributeKey<AtProtoProxyPlugin>("AtprotoProxyPlugin")

        override fun prepare(block: Unit.() -> Unit): AtProtoProxyPlugin = AtProtoProxyPlugin()

        override fun install(
            plugin: AtProtoProxyPlugin,
            scope: HttpClient,
        ) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                if (context.url.pathSegments
                        .lastOrNull()
                        ?.startsWith("chat.bsky.convo.") == true
                ) {
                    context.headers["Atproto-Proxy"] = "did:web:api.bsky.chat#bsky_chat"
                }
            }
        }
    }
}
