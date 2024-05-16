package com.zhangke.utopia.status.platform

import android.os.Parcelable
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.model.StatusProviderProtocol
import kotlinx.parcelize.Parcelize
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
) : Parcelable, java.io.Serializable
