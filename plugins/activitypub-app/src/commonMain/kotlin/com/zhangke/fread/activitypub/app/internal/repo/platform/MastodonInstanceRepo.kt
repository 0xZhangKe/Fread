package com.zhangke.fread.activitypub.app.internal.repo.platform

import com.zhangke.framework.architect.http.sharedHttpClient
import com.zhangke.fread.activitypub.app.createActivityPubProtocol
import com.zhangke.fread.status.platform.PlatformSnapshot
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.takeFrom
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject

class MastodonInstanceRepo @Inject constructor() {

    private companion object {

        private const val BASE_URL = "https://instances.social/api/1.0"
        private const val PATH_SEARCH = "$BASE_URL/instances/search"
        private const val TOKEN =
            "kw1opiSJ24Gwddb49rRCbOZlFrI6cCxSxprWhsGMDzKQA36obwpcFr9DldvIwUDAXvbprvwCEUCUZ4eKQYr1vxZ8QP5PkD3dGUKlaETF4MAMsKJXEFiY5gKfzYL8MoUE"
    }

    suspend fun searchAll(query: String): Result<List<PlatformSnapshot>> {
        return runCatching {
            sharedHttpClient.get {
                url(PATH_SEARCH) {
                    parameter("q", query)
                    parameter("name", false)
                    parameter("count", 120)
                }
                applyCommonHeaders()
            }.body<QueryResult>()
                .instances
                .map { it.toPlatformSnapshot() }
        }
    }

    suspend fun searchWithName(name: String): Result<List<PlatformSnapshot>> {
        return runCatching {
            sharedHttpClient.get {
                url {
                    takeFrom(PATH_SEARCH)
                    parameter("q", name)
                    parameter("name", true)
                    parameter("count", 20)
                }
                applyCommonHeaders()
            }.body<QueryResult>()
                .instances
                .map { it.toPlatformSnapshot() }
        }
    }

    private fun HttpRequestBuilder.applyCommonHeaders() {
        header("Authorization", "Bearer $TOKEN")
    }

    private suspend fun MastodonInstance.toPlatformSnapshot(): PlatformSnapshot {
        return PlatformSnapshot(
            domain = name,
            description = info?.shortDescription.orEmpty(),
            thumbnail = thumbnail.orEmpty(),
            protocol = createActivityPubProtocol(),
        )
    }

    @Serializable
    data class QueryResult(
        val instances: List<MastodonInstance>,
    )

    @Serializable
    data class MastodonInstance(
        val name: String,
        val info: Info?,
        val thumbnail: String?,
        val version: String?,
        val users: Int?,
        @SerialName("active_users")
        val activeUsers: Int?,
    ) {

        @Serializable
        data class Info(
            @SerialName("short_description")
            val shortDescription: String?,
            val languages: List<String>?,
            val categories: List<String>?,
        )
    }
}
