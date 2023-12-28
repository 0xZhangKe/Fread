package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.framework.network.FormalBaseUrl

data class ActivityPubApplication(
    val baseUrl: FormalBaseUrl,
    val id: String,
    val name: String,
    val website: String,
    val redirectUri: String,
    val clientId: String,
    val clientSecret: String,
    val vapidKey: String,
)
