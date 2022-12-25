package com.zhangke.activitypub.api

import com.zhangke.activitypub.entry.RegisterApplicationEntry
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by ZhangKe on 2022/12/2.
 */
private interface RegisterApi {

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

class RegisterRepo(retrofit: Retrofit) {

    private val api = retrofit.create(RegisterApi::class.java)

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