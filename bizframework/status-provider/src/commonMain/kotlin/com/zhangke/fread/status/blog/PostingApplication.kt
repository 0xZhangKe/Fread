package com.zhangke.fread.status.blog

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
data class PostingApplication(
    val name: String,
    val website: String?,
) : PlatformSerializable
