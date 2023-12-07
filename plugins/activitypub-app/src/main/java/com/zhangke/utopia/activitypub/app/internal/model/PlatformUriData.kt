package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.utopia.status.uri.StatusProviderUri

data class PlatformUriData(
    val uri: StatusProviderUri,
    val serverBaseUrl: String,
)
