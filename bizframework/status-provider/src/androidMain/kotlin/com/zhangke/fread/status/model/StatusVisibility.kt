package com.zhangke.fread.status.model

import kotlinx.serialization.Serializable

@Serializable
enum class StatusVisibility : java.io.Serializable {
    PUBLIC,
    UNLISTED,
    PRIVATE,
    DIRECT,
}
