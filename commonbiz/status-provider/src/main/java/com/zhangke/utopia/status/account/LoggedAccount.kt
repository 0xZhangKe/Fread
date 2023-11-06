package com.zhangke.utopia.status.account

import com.zhangke.utopia.status.platform.UtopiaPlatform
import com.zhangke.utopia.status.uri.StatusProviderUri

open class LoggedAccount(
    val userId: String,
    val uri: StatusProviderUri,
    val platform: UtopiaPlatform,
    val host: String,
    val name: String,
    val description: String?,
    val avatar: String?,
    val homepage: String?,
    val active: Boolean,
)
