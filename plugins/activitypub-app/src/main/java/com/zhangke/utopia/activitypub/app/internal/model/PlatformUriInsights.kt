package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.utopia.status.uri.StatusProviderUri

data class PlatformUriInsights(
    val uri: StatusProviderUri,
    val serverBaseUrl: String,
)
