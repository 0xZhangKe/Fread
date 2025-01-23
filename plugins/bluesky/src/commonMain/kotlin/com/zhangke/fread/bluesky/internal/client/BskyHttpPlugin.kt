package com.zhangke.fread.bluesky.internal.client

import com.atproto.server.RefreshSessionResponse
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.body
import io.ktor.client.call.save
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import sh.christian.ozone.api.response.AtpErrorDescription

internal class AtProtoProxyPlugin {
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

internal class XrpcAuthPlugin(
    private val json: Json,
    private val accountProvider: suspend () -> BlueskyLoggedAccount?,
    private val newSessionUpdater: suspend (RefreshSessionResponse) -> Unit,
    private val onLoginRequest: suspend () -> Unit,
) {

    class Config(
        var json: Json? = null,
        var accountProvider: suspend () -> BlueskyLoggedAccount? = { null },
        var newSessionUpdater: suspend (RefreshSessionResponse) -> Unit = {},
        var onLoginRequest: suspend () -> Unit = {},
    )

    companion object : HttpClientPlugin<Config, XrpcAuthPlugin> {

        private const val REFRESH_TOKEN_METHOD = "com.atproto.server.refreshSession"
        private const val REFRESH_TOKEN_PATH = "/xrpc/$REFRESH_TOKEN_METHOD"

        override val key = AttributeKey<XrpcAuthPlugin>("XrpcAuthPlugin")

        override fun prepare(block: Config.() -> Unit): XrpcAuthPlugin {
            val config = Config().apply(block)
            return XrpcAuthPlugin(
                config.json!!,
                config.accountProvider,
                config.newSessionUpdater,
                config.onLoginRequest,
            )
        }

        override fun install(plugin: XrpcAuthPlugin, scope: HttpClient) {
            scope.plugin(HttpSend).intercept { context ->

                if (!context.headers.contains(Authorization)) {
                    val account = plugin.accountProvider()
                    if (account != null) {
                        context.bearerAuth(account.accessJwt)
                    }
                }

                var result: HttpClientCall = execute(context)
                if (result.response.status != BadRequest) {
                    return@intercept result
                }

                result = result.save()

                val response = runCatching<AtpErrorDescription> {
                    plugin.json.decodeFromString(result.response.bodyAsText())
                }
                if (response.getOrNull()?.expired == true) {
                    if (context.isRefreshTokenRequest) {
                        plugin.onLoginRequest()
                    } else {
                        val account = plugin.accountProvider()
                        if (account != null) {
                            refreshToken(scope, account.refreshJwt)?.let { response ->
                                plugin.newSessionUpdater(response)
                                context.headers.remove(Authorization)
                                context.bearerAuth(response.accessJwt)
                                result = execute(context)
                            }
                        }
                    }
                }
                result
            }
        }

        private val HttpRequestBuilder.isRefreshTokenRequest: Boolean
            get() = url.pathSegments.contains(REFRESH_TOKEN_METHOD)

        private suspend fun refreshToken(
            scope: HttpClient,
            refreshToken: String,
        ): RefreshSessionResponse? {
            return runCatching {
                scope.post(REFRESH_TOKEN_PATH) {
                    bearerAuth(refreshToken)
                }.body<RefreshSessionResponse>()
            }.getOrNull()
        }
    }
}
