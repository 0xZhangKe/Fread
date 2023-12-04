package com.zhangke.utopia.activitypub.app.internal.model

data class ActivityPubApplication(
    val baseUrl: String,
    val id: String,
    val name: String,
    val website: String,
    val redirectUri: String,
    val clientId: String,
    val clientSecret: String,
    val vapidKey: String,
)
