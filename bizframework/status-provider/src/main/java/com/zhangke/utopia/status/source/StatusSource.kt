package com.zhangke.utopia.status.source

import android.os.Parcelable
import com.zhangke.utopia.status.model.StatusProviderProtocol
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.parcelize.Parcelize

@Parcelize
data class StatusSource(
    val uri: FormalUri,
    val name: String,
    val description: String,
    val thumbnail: String?,
    val protocol: StatusProviderProtocol,
): Parcelable
