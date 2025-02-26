package com.zhangke.framework.utils

import kotlinx.serialization.Serializable

@Serializable
data class AspectRatio(
    val width: Long,
    val height: Long,
) : PlatformSerializable, PlatformParcelable {

    val ratio: Float get() = width.toFloat() / height

    init {
        require(width >= 1) {
            "width must be >= 1, but was $width"
        }
        require(height >= 1) {
            "height must be >= 1, but was $height"
        }
    }
}
