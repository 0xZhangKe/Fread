package com.zhangke.activitypub

/**
 * Created by ZhangKe on 2022/12/4.
 */

data class ActivityPubApplication(
    val domain: String,
    val id: String,
    val name: String,
    val website: String,
    val redirectUri: String,
    val clientId: String,
    val clientSecret: String,
    val vapidKey: String,
)