package com.zhangke.utopia.status.user

open class UtopiaUser(
    val uri: String,
    val userName: String,
    val description: String,
    val homePageUrl: String,
    val avatar: String,
    val header: String,
    val followersCount: Int,
    val followingCount: Int,
    val statusesCount: Int?,
)
