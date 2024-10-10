package com.zhangke.fread.activitypub.app.internal.model

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.Parcelize
import com.zhangke.framework.utils.PlatformParcelable
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UserUriInsights(
    val uri: FormalUri,
    val webFinger: WebFinger,
    val baseUrl: FormalBaseUrl,
) : PlatformParcelable
