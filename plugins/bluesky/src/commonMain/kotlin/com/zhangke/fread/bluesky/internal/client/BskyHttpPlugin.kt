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
import sh.christian.ozone.api.response.StatusCode

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

/**
 * Adds the `atproto-accept-labelers` header to every outgoing request based
 * on the subscribed labelers cached in [BlueskyLabelersCache]. Required for
 * custom labelers (e.g. skywatch.blue) to actually return labels —
 * Bluesky's AppView only honours labels from labelers listed here.
 */
internal class LabelersHeaderPlugin private constructor(
    private val cache: BlueskyLabelersCache,
) {

    class Config {
        var cache: BlueskyLabelersCache? = null
    }

    companion object : HttpClientPlugin<Config, LabelersHeaderPlugin> {

        private const val HEADER = "atproto-accept-labelers"

        override val key = AttributeKey<LabelersHeaderPlugin>("LabelersHeaderPlugin")

        override fun prepare(block: Config.() -> Unit): LabelersHeaderPlugin {
            val config = Config().apply(block)
            return LabelersHeaderPlugin(config.cache!!)
        }

        override fun install(plugin: LabelersHeaderPlugin, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                val labelers = plugin.cache.activeLabelers()
                if (labelers.isNotEmpty() && !context.headers.contains(HEADER)) {
                    context.headers[HEADER] = labelers.joinToString(", ")
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
                        if (context.isRefreshTokenRequest) {
                            context.bearerAuth(account.refreshJwt)
                        } else {
                            context.bearerAuth(account.accessJwt)
                        }
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
                            refreshToken(scope, account.refreshJwt)?.let { refreshedResponse ->
                                plugin.newSessionUpdater(refreshedResponse)
                                context.headers.remove(Authorization)
                                context.bearerAuth(refreshedResponse.accessJwt)
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
                val response = scope.post(REFRESH_TOKEN_PATH) {
                    bearerAuth(refreshToken)
                }
                if (StatusCode.fromCode(response.status.value) == StatusCode.Okay) {
                    response.body<RefreshSessionResponse>()
                } else {
                    null
                }
            }.getOrNull()
        }
    }
}
