package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.uri.FormalUri

data class PlatformUriInsights(
    val uri: FormalUri,
    val serverBaseUrl: FormalBaseUrl,
)
