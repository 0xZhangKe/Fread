package com.zhangke.fread.status.source

import android.os.Parcelable
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.parcelize.Parcelize

@Parcelize
data class StatusSource(
    val uri: FormalUri,
    val name: String,
    val description: String,
    val thumbnail: String?,
    val protocol: StatusProviderProtocol,
): Parcelable
