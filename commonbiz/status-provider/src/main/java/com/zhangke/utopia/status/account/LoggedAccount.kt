package com.zhangke.utopia.status.account

import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.StatusProviderUri

open class LoggedAccount(
    val uri: StatusProviderUri,
    val platform: BlogPlatform,
    val userName: String,
    val description: String?,
    val avatar: String?,
    val active: Boolean,
)
