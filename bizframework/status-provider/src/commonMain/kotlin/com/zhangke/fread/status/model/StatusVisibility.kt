package com.zhangke.fread.status.model

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
enum class StatusVisibility : PlatformSerializable {
    PUBLIC,
    UNLISTED,
    PRIVATE,
    DIRECT,
}
