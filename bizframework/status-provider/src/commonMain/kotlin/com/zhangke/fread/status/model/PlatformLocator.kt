package com.zhangke.fread.status.model

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.Parcelize
import com.zhangke.framework.utils.PlatformParcelable
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PlatformLocator(
    val baseUrl: FormalBaseUrl,
    val accountUri: FormalUri? = null,
) : PlatformParcelable, PlatformSerializable
