package com.zhangke.utopia.activitypubapp.model

import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.user.UtopiaUser

class ActivityPubUser(
    val id: String,
    uri: String,
    val webFinger: WebFinger,
    userName: String,
    val displayName: String,
    val locked: Boolean,
    val bot: Boolean,
    val discoverable: Boolean,
    val group: Boolean,
    val createdAt: String,
    val note: String,
    homePageUrl: String,
    avatar: String,
    val avatarStatic: String,
    header: String,
    val headerStatic: String,
    followersCount: Int,
    followingCount: Int,
    statusesCount: Int,
    val lastStatusAt: String,
) : UtopiaUser(
    uri = uri,
    userName = userName,
    description = note,
    homePageUrl = homePageUrl,
    avatar = avatar,
    header = header,
    followersCount = followersCount,
    followingCount = followingCount,
    statusesCount = statusesCount,
)
