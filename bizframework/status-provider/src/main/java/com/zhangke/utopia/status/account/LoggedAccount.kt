package com.zhangke.utopia.status.account

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.FormalUri

open class LoggedAccount(
    val uri: FormalUri,
    val webFinger: WebFinger,
    val platform: BlogPlatform,
    val userName: String,
    val description: String?,
    val avatar: String?,
)
