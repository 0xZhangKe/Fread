package com.zhangke.fread.status.blog

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
data class BlogMedia(
    val id: String,
    val url: String,
    val type: BlogMediaType,
    val previewUrl: String?,
    val remoteUrl: String?,
    val description: String?,
    val blurhash: String?,
    val meta: BlogMediaMeta?,
): PlatformSerializable