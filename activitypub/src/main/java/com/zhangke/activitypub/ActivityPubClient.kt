package com.zhangke.activitypub

import com.google.gson.Gson
import com.zhangke.activitypub.api.OAuthRepo
import com.zhangke.activitypub.utils.ResultCallAdapterFactory
import com.zhangke.activitypub.utils.activityPubGson
import retrofit2.Retrofit

/**
 * Created by ZhangKe on 2022/12/3.
 */
class ActivityPubClient(
    val application: ActivityPubApplication,
    retrofit: Retrofit,
    gson: Gson = activityPubGson,
    oauthRequest: (url: String) -> String
) {

    private val _retrofit: Retrofit = retrofit.newBuilder()
        .addCallAdapterFactory(ResultCallAdapterFactory(gson))
        .build()

    val oauthRepo: OAuthRepo by lazy { OAuthRepo(_retrofit) }

}