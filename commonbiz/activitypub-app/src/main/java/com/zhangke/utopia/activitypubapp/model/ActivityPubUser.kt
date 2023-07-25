package com.zhangke.utopia.activitypubapp.model

import com.zhangke.utopia.activitypubapp.uri.user.ActivityPubUserUri
import com.zhangke.utopia.activitypubapp.utils.WebFinger

data class ActivityPubUser(
    val id: String,
    val uri: ActivityPubUserUri,
    val webFinger: WebFinger,
    val username: String,
    val displayName: String,
    val locked: Boolean,
    val bot: Boolean,
    val discoverable: Boolean,
    val group: Boolean,
    val createdAt: String,
    val note: String,
    val homePageUrl: String,
    val avatar: String,
    val avatarStatic: String,
    val header: String,
    val headerStatic: String,
    val followersCount: Int,
    val followingCount: Int,
    val statusesCount: Int,
    val lastStatusAt: String,
)
