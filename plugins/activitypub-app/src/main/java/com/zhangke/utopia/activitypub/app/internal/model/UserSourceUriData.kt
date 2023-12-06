package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.uri.StatusProviderUri

data class UserSourceUriData(
    val uri: StatusProviderUri,
    val userId: String,
    val webFinger: WebFinger,
)
