package com.zhangke.activitypub.api

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entry.ActivityPubAccount
import com.zhangke.activitypub.entry.ActivityPubStatus
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by ZhangKe on 2022/12/13.
 */
private interface AccountsApi {

    @GET("/api/v1/accounts/lookup")
    suspend fun lookup(@Query("acct") acct: String): Result<ActivityPubAccount>

    @GET("/api/v1/accounts/{id}")
    suspend fun getAccount(@Path("id") id: String): Result<ActivityPubAccount>

    @GET("/api/v1/accounts/{id}/statuses")
    suspend fun getStatuses(
        @Path("id") id: String,
        @Query("limit") limit: Int
    ): Result<List<ActivityPubStatus>>
}

class AccountsRepo(client: ActivityPubClient) : ActivityPubRepo(client) {

    private val api = createApi(AccountsApi::class.java)

    suspend fun lookup(acct: String): Result<ActivityPubAccount> {
        return api.lookup(acct).collectAuthorizeFailed()
    }

    suspend fun getAccount(id: String): Result<ActivityPubAccount> {
        return api.getAccount(id).collectAuthorizeFailed()
    }

    suspend fun getStatuses(id: String, limit: Int = 20): Result<List<ActivityPubStatus>> {
        return api.getStatuses(id, limit).collectAuthorizeFailed()
    }
}