package com.zhangke.utopia.status.user

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.uri.StatusProviderUri

open class UtopiaUser(
    val uri: StatusProviderUri,
    val webFinger: WebFinger,
    val userName: String,
    val description: String,
    val homePageUrl: String,
    val avatar: String,
    val header: String,
    val followersCount: Int,
    val followingCount: Int,
    val statusesCount: Int?,
)
