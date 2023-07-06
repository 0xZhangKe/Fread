package com.zhangke.utopia.status.user

import com.zhangke.utopia.status.platform.UtopiaPlatform

data class LoggedAccount(
    val id: String,
    val platform: UtopiaPlatform,
    val host: String,
    val name: String,
    val description: String?,
    val avatar: String?,
    val homepage: String?,
    val active: Boolean,
    val validate: Boolean,
)
