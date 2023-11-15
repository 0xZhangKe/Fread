package com.zhangke.utopia.status.account

import com.zhangke.utopia.status.platform.UtopiaPlatform
import com.zhangke.utopia.status.uri.StatusProviderUri

open class LoggedAccount(
    val uri: StatusProviderUri,
    val platform: UtopiaPlatform,
    val userName: String,
    val description: String?,
    val avatar: String?,
    val active: Boolean,
)
