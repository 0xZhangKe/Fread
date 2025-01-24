package com.zhangke.fread.bluesky.internal.client

import app.bsky.actor.GetPreferencesResponse
import app.bsky.actor.GetProfileQueryParams
import app.bsky.actor.ProfileViewDetailed
import app.bsky.actor.PutPreferencesRequest
import app.bsky.feed.GetActorFeedsQueryParams
import app.bsky.feed.GetActorFeedsResponse
import app.bsky.feed.GetAuthorFeedQueryParams
import app.bsky.feed.GetAuthorFeedResponse
import app.bsky.feed.GetFeedGeneratorsQueryParams
import app.bsky.feed.GetFeedGeneratorsResponse
import app.bsky.feed.GetFeedQueryParams
import app.bsky.feed.GetFeedResponse
import app.bsky.feed.GetListFeedQueryParams
import app.bsky.feed.GetListFeedResponse
import app.bsky.feed.GetPostThreadQueryParams
import app.bsky.feed.GetPostThreadResponse
import app.bsky.feed.GetSuggestedFeedsQueryParams
import app.bsky.feed.GetSuggestedFeedsResponse
import app.bsky.feed.GetTimelineQueryParams
import app.bsky.feed.GetTimelineResponse
import app.bsky.graph.GetListQueryParams
import app.bsky.graph.GetListResponse
import app.bsky.graph.GetListsQueryParams
import app.bsky.graph.GetListsResponse
import app.bsky.unspecced.GetPopularFeedGeneratorsQueryParams
import app.bsky.unspecced.GetPopularFeedGeneratorsResponse
import com.atproto.repo.CreateRecordRequest
import com.atproto.repo.CreateRecordResponse
import com.atproto.repo.DeleteRecordRequest
import com.atproto.repo.DeleteRecordResponse
import com.atproto.repo.GetRecordQueryParams
import com.atproto.repo.GetRecordResponse
import com.atproto.repo.PutRecordRequest
import com.atproto.repo.PutRecordResponse
import com.atproto.server.CreateSessionRequest
import com.atproto.server.CreateSessionResponse
import com.atproto.server.RefreshSessionResponse
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.utils.toResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.Url
import kotlinx.serialization.json.Json
import sh.christian.ozone.BlueskyApi
import sh.christian.ozone.XrpcBlueskyApi
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

    suspend fun putPreferencesCatching(request: PutPreferencesRequest): Result<Unit> {
        return runCatching { putPreferences(request) }.toResult()
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

    suspend fun getSuggestedFeedsCatching(params: GetSuggestedFeedsQueryParams): Result<GetSuggestedFeedsResponse> {
        return runCatching { getSuggestedFeeds(params) }.toResult()
    }

    suspend fun getListsCatching(request: GetListsQueryParams): Result<GetListsResponse> {
        return runCatching { getLists(request) }.toResult()
    }

    suspend fun getListCatching(params: GetListQueryParams): Result<GetListResponse> {
        return runCatching { getList(params) }.toResult()
    }

    suspend fun getListFeedCatching(params: GetListFeedQueryParams): Result<GetListFeedResponse> {
        return runCatching { getListFeed(params) }.toResult()
    }

    suspend fun getRecordCatching(params: GetRecordQueryParams): Result<GetRecordResponse> {
        return runCatching { getRecord(params) }.toResult()
    }

    suspend fun createRecordCatching(params: CreateRecordRequest): Result<CreateRecordResponse> {
        return runCatching { createRecord(params) }.toResult()
    }

    suspend fun putRecordCatching(params: PutRecordRequest): Result<PutRecordResponse> {
        return runCatching { putRecord(params) }.toResult()
    }

    suspend fun deleteRecordCatching(params: DeleteRecordRequest): Result<DeleteRecordResponse> {
        return runCatching { deleteRecord(params) }.toResult()
    }

    suspend fun getPostThreadCatching(params: GetPostThreadQueryParams): Result<GetPostThreadResponse> {
        return runCatching { getPostThread(params) }.toResult()
    }

    private fun <T : Any> Result<AtpResponse<T>>.toResult(): Result<T> {
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
