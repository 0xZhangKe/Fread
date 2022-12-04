package com.zhangke.activitypub.api

import com.google.gson.JsonObject
import com.zhangke.activitypub.entry.RegisterApplicationEntry
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * Created by ZhangKe on 2022/12/2.
 */
private interface OAuthApi {

    //TODO 需要考虑这个接口
    @FormUrlEncoded
    @POST("/api/v1/apps")
    suspend fun registerApplication(
        @Field("client_name") clientName: String,
        @Field("redirect_uris") redirectUris: String,
        @Field("scopes") scopes: String,
        @Field("website") website: String
    ): Result<RegisterApplicationEntry>


}

class OAuthRepo(retrofit: Retrofit) {

    private val api = retrofit.create(OAuthApi::class.java)

    suspend fun registerApplication(
        clientName: String,
        redirectUris: List<String>,
        scopes: List<String>,
        website: String
    ): Result<RegisterApplicationEntry> {
        val redirectUrisString = redirectUris.joinToString(":")
        val scopesString = scopes.joinToString(" ")
        return api.registerApplication(clientName, redirectUrisString, scopesString, website)
    }

    fun startOAuth(){
        // https://m.cmx.im/oauth/authorize?response_type=code&client_id=SgKN04a7lufOplcl_VEp41oTJfHlAr0k1vc6NVdss0g&redirect_uri=https://0xzhangke.github.io/&scope=read+write+follow+push

    }

    /**
     * https://docs.joinmastodon.org/api/oauth-scopes/#granular
     */
    object AppScopes {

        const val READ = "read"

        const val WRITE = "write"

        const val FOLLOW = "follow"

        const val PUSH = "push"

        val ALL = listOf(READ, WRITE, FOLLOW, PUSH)
    }
}