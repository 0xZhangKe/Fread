package com.zhangke.utopia.activitypubapp.user

import com.zhangke.activitypub.entry.ActivityPubToken
import com.zhangke.utopia.status.auth.UtopiaUser

class ActivityPubUser(
    domain: String,
    name: String,
    id: String,
    val token: ActivityPubToken,
    avatar: String?,
    description: String?,
    homePage: String?,
    selected: Boolean,
) : UtopiaUser(
    domain = domain,
    name = name,
    id = id,
    avatar = avatar,
    description = description,
    homePage = homePage,
    selected = selected
)