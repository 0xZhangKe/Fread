package com.zhangke.activitypub.api

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entry.ActivityPubStatus
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Created by ZhangKe on 2022/12/13.
 */

private interface TimelinesApi {

    @GET("/api/v1/timelines/public?only_media=false")
    suspend fun timelines(
        @Header("Authorization") authorization: String,
        @Query("local") local: Boolean,
        @Query("remote") remote: Boolean,
        @Query("limit") limit: Int
    ): Result<List<ActivityPubStatus>>

}

class TimelinesRepo(client: ActivityPubClient) : ActivityPubBaseRepo(client) {

    private val api = createApi(TimelinesApi::class.java)

    /**
     * 本站timelines
     */
    suspend fun localTimelines(limit: Int = 20): Result<List<ActivityPubStatus>> {
        return api.timelines(
            authorization = getAuthorizationHeader(),
            local = true,
            remote = false,
            limit
        ).collectAuthorizeFailed()
    }

    /**
     * 跨站timelines
     */
    suspend fun publicTimelines(limit: Int = 20): Result<List<ActivityPubStatus>> {
        return api.timelines(
            authorization = getAuthorizationHeader(),
            local = false,
            remote = true,
            limit
        ).collectAuthorizeFailed()
    }
}