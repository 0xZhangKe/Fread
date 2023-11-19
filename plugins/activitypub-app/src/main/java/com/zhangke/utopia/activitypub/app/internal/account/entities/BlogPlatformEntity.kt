package com.zhangke.utopia.activitypub.app.internal.account.entities

data class BlogPlatformEntity(
    val uri: String,
    val name: String,
    val description: String,
    val baseUrl: String,
    val protocol: String,
    val thumbnail: String?,
)
