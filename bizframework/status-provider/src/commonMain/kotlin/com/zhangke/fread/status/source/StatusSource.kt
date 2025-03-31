package com.zhangke.fread.status.source

import com.zhangke.framework.utils.Parcelize
import com.zhangke.framework.utils.PlatformParcelable
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.uri.FormalUri

@Parcelize
data class StatusSource(
    val uri: FormalUri,
    val name: String,
    val handle: String,
    val description: String,
    val thumbnail: String?,
    val protocol: StatusProviderProtocol,
): PlatformParcelable, PlatformSerializable
