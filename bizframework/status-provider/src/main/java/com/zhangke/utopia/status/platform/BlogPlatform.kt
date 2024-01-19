package com.zhangke.utopia.status.platform

import android.os.Parcelable
import com.zhangke.framework.network.FormalBaseUrl
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class BlogPlatform(
    val uri: String,
    val name: String,
    val description: String,
    val baseUrl: FormalBaseUrl,
    val protocol: String,
    val thumbnail: String?,
) : Parcelable
