package com.zhangke.utopia.status.status

import com.zhangke.utopia.status.blog.BlogMediaType

data class UploadAttachmentMediaResult(
    val id: String,
    val mediaType: BlogMediaType,
    val url: String,
    val description: String?,
    val blurhash: String?,
)
