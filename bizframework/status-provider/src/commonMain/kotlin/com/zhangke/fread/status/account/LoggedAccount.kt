package com.zhangke.fread.status.account

import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri

open class LoggedAccount(
    val uri: FormalUri,
    val webFinger: WebFinger,
    val platform: BlogPlatform,
    val userName: String,
    val description: String?,
    val avatar: String?,
    val emojis: List<Emoji>,
)
