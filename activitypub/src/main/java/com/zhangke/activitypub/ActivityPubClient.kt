package com.zhangke.activitypub

import com.google.gson.Gson
import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.activitypub.api.OAuthRepo
import com.zhangke.activitypub.api.RegisterRepo
import com.zhangke.activitypub.api.TimelinesRepo
import com.zhangke.activitypub.entry.ActivityPubToken
import com.zhangke.activitypub.utils.ResultCallAdapterFactory
import com.zhangke.activitypub.utils.activityPubGson
import retrofit2.Retrofit

private const val REDIRECT_URI = "utopia://oauth.utopia"

/**
 * Created by ZhangKe on 2022/12/3.
 */
class ActivityPubClient(
    val application: ActivityPubApplication,
    val baseUrl: String,
    retrofit: Retrofit,
    gson: Gson = activityPubGson,
    val onAuthorizeFailed: (url: String) -> Unit
) {

    internal val retrofit: Retrofit = retrofit.newBuilder()
        .addCallAdapterFactory(ResultCallAdapterFactory(gson))
        .build()

    var token: ActivityPubToken? = null

    val oauthRepo: OAuthRepo by lazy { OAuthRepo(this) }

    val accountRepo: AccountsRepo by lazy { AccountsRepo(this) }

    val timelinesRepo: TimelinesRepo by lazy { TimelinesRepo(this) }

    internal fun buildOAuthUrl(): String {
        //https://m.cmx.im/oauth/authorize?response_type=code&client_id=KHGSFM7oZY2_ZhaQRo25DfBRNwERZy7_iqZ_HjA5Sp8&redirect_uri=utopia://oauth.utopia&scope=read+write+follow+push
        val baseUrl = baseUrl.removeSuffix("/")
        return "${baseUrl}/oauth/authorize" +
                "?response_type=code" +
                "&client_id=${application.clientId}" +
                "&redirect_uri=$REDIRECT_URI" +
                "&scope=read+write+follow+push"
    }
}