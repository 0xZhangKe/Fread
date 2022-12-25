package com.zhangke.activitypub.api

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entry.ActivityPubToken
import retrofit2.http.GET

/**
 * Created by ZhangKe on 2022/12/14.
 */

private interface OAuthApi {

    @GET("/oauth/token")
    fun getToken(): Result<ActivityPubToken>

}

class OAuthRepo(client: ActivityPubClient) : ActivityPubRepo(client) {

    private val api = createApi(OAuthRepo::class.java)

    fun getToken(): Result<ActivityPubToken> {
        return api.getToken()
    }
}