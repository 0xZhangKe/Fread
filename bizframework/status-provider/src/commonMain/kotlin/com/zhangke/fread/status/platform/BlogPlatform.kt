package com.zhangke.fread.status.platform

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.Parcelize
import com.zhangke.framework.utils.PlatformParcelable
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.status.model.StatusProviderProtocol
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class BlogPlatform(
    val uri: String,
    val name: String,
    val description: String,
    val baseUrl: FormalBaseUrl,
    val protocol: StatusProviderProtocol,
    val thumbnail: String?,
    val supportsQuotePost: Boolean? = null,
) : PlatformParcelable, PlatformSerializable
