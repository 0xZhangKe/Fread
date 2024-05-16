package com.zhangke.utopia.status.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class StatusProviderProtocol(
    val id: String,
    val name: String,
) : Parcelable, java.io.Serializable
