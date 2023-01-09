package com.zhangke.activitypub.api

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entry.ActivityPubToken
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by ZhangKe on 2022/12/14.
 */

private interface OAuthApi {

    @FormUrlEncoded
    @POST("/oauth/token")
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("scope") scope: String,
    ): Result<ActivityPubToken>

}

class OAuthRepo(client: ActivityPubClient) : ActivityPubBaseRepo(client) {

    private val api = createApi(OAuthApi::class.java)

    suspend fun getToken(code: String, scopeList: Array<ActivityPubScope>): Result<ActivityPubToken> {
        return api.getToken(
            clientId = client.application.clientId,
            clientSecret = client.application.clientSecret,
            redirectUri = client.application.redirectUri,
            grantType = "authorization_code",
            code = code,
            scope = scopeList.joinToString("+") { it.scope }
        )
    }
}