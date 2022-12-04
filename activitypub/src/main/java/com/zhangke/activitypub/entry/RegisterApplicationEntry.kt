package com.zhangke.activitypub.entry

import com.google.gson.annotations.SerializedName

/**
 * Created by ZhangKe on 2022/12/4.
 */
data class RegisterApplicationEntry(
    val id: String,
    val name: String,
    val website: String,
    @SerializedName("redirect_uri") val redirectUri: String,
    @SerializedName("client_id") val clientId: String,
    @SerializedName("client_secret") val clientSecret: String,
    @SerializedName("vapid_key") val vapidKey: String,
)