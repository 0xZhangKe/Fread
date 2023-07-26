package com.zhangke.utopia.status.account

import com.zhangke.utopia.status.platform.UtopiaPlatform

open class LoggedAccount(
    val userId: String,
    val uri: String,
    val platform: UtopiaPlatform,
    val host: String,
    val name: String,
    val description: String?,
    val avatar: String?,
    val homepage: String?,
    val active: Boolean,
)
