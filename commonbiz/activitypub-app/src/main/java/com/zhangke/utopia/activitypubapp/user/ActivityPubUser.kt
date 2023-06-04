package com.zhangke.utopia.activitypubapp.user

import com.zhangke.activitypub.entry.ActivityPubToken

class ActivityPubUser(
    domain: String,
    name: String,
    id: String,
    val token: ActivityPubToken,
    avatar: String?,
    description: String?,
    homePage: String?,
    selected: Boolean,
)
