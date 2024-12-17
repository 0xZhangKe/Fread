package com.zhangke.fread.bluesky.internal.client

import app.bsky.actor.GetPreferencesResponse
import app.bsky.actor.GetProfileQueryParams
import app.bsky.actor.ProfileViewDetailed
import app.bsky.feed.GetActorFeedsQueryParams
import app.bsky.feed.GetActorFeedsResponse
import app.bsky.feed.GetAuthorFeedQueryParams
import app.bsky.feed.GetAuthorFeedResponse
import app.bsky.feed.GetFeedGeneratorsQueryParams
import app.bsky.feed.GetFeedGeneratorsResponse
import app.bsky.feed.GetFeedQueryParams
import app.bsky.feed.GetFeedResponse
import app.bsky.feed.GetTimelineQueryParams
import app.bsky.feed.GetTimelineResponse
import app.bsky.graph.GetListsQueryParams
import app.bsky.graph.GetListsResponse
import app.bsky.unspecced.GetPopularFeedGeneratorsQueryParams
import app.bsky.unspecced.GetPopularFeedGeneratorsResponse
import com.atproto.server.CreateSessionRequest
import com.atproto.server.CreateSessionResponse
import com.atproto.server.RefreshSessionResponse
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.utils.toResult
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.body
import io.ktor.client.call.save
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.Url
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import sh.christian.ozone.BlueskyApi
import sh.christian.ozone.XrpcBlueskyApi
import sh.christian.ozone.api.response.AtpErrorDescription
import sh.christian.ozone.api.response.AtpResponse

class BlueskyClient(
    val baseUrl: FormalBaseUrl,
    private val engine: HttpClientEngine,
    val json: Json,
    val loggedAccountProvider: suspend () -> BlueskyLoggedAccount?,
    val newSessionUpdater: suspend (RefreshSessionResponse) -> Unit,
    val onLoginRequest: suspend () -> Unit,
) : BlueskyApi by XrpcBlueskyApi(
    createBlueskyHttpClient(
        engine,
        json,
        baseUrl.toString(),
        loggedAccountProvider,
        newSessionUpdater,
        onLoginRequest,
    )
) {

    suspend fun createSessionCatching(request: CreateSessionRequest): Result<CreateSessionResponse> {
        return runCatching { createSession(request) }.toResult()
    }

    suspend fun getProfileCatching(request: GetProfileQueryParams): Result<ProfileViewDetailed> {
        return runCatching { getProfile(request) }.toResult()
    }

    suspend fun getTimelineCatching(request: GetTimelineQueryParams): Result<GetTimelineResponse> {
        return runCatching { getTimeline(request) }.toResult()
    }

    suspend fun getPreferencesCatching(): Result<GetPreferencesResponse> {
        return runCatching { getPreferences() }.toResult()
    }

    suspend fun getFeedGeneratorsCatching(params: GetFeedGeneratorsQueryParams): Result<GetFeedGeneratorsResponse> {
        return runCatching { getFeedGenerators(params) }.toResult()
    }

    suspend fun getFeedCatching(params: GetFeedQueryParams): Result<GetFeedResponse> {
        return runCatching { getFeed(params) }.toResult()
    }

    suspend fun getPopularFeedGeneratorsCatching(params: GetPopularFeedGeneratorsQueryParams): Result<GetPopularFeedGeneratorsResponse> {
        return runCatching { getPopularFeedGenerators(params) }.toResult()
    }

    suspend fun getActorFeedsCatching(request: GetActorFeedsQueryParams): Result<GetActorFeedsResponse> {
        return runCatching { getActorFeeds(request) }.toResult()
    }

    suspend fun getAuthorFeedCatching(request: GetAuthorFeedQueryParams): Result<GetAuthorFeedResponse> {
        return runCatching { getAuthorFeed(request) }.toResult()
    }

    suspend fun getListsCatching(request: GetListsQueryParams): Result<GetListsResponse> {
        return runCatching { getLists(request) }.toResult()
    }

    fun <T : Any> Result<AtpResponse<T>>.toResult(): Result<T> {
        if (this.isFailure) return Result.failure(this.exceptionOrThrow())
        return this.getOrThrow().toResult()
    }
}

private fun createBlueskyHttpClient(
    engine: HttpClientEngine,
    json: Json,
    baseUrl: String,
    accountProvider: suspend () -> BlueskyLoggedAccount?,
    newSessionUpdater: suspend (RefreshSessionResponse) -> Unit,
    onLoginRequest: suspend () -> Unit,
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
        install(XrpcAuthPlugin) {
            this.json = json
            this.accountProvider = accountProvider
            this.newSessionUpdater = newSessionUpdater
            this.onLoginRequest = onLoginRequest
        }
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

private class XrpcAuthPlugin(
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
                if (response.getOrNull()?.expired == true && !context.isRefreshTokenRequest) {
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
