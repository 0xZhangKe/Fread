package com.zhangke.fread.status.platform

import arrow.core.Either
import com.zhangke.fread.status.model.StatusProviderProtocol
import org.jetbrains.compose.resources.DrawableResource

data class PlatformSnapshot (
    val domain: String,
    val description: String,
    val thumbnail: Either<String, DrawableResource>,
    val protocol: StatusProviderProtocol,
    val priority: Int = 0,
)
