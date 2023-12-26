package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.uri.FormalUri

data class UserUriInsights(
    val uri: FormalUri,
    val webFinger: WebFinger,
)
