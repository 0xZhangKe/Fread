package com.zhangke.fread.bluesky.internal.uri.platform

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.status.uri.FormalUri

data class PlatformUriInsights(
    val uri: FormalUri,
    val serverBaseUrl: FormalBaseUrl,
)
