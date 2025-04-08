package com.zhangke.fread.commonbiz.shared.screen.publish.model

sealed interface PublishBlogMediaAttachment {

    data class Image(val files: List<PublishBlogMediaAttachmentFile>) : PublishBlogMediaAttachment

    data class Video(val file: PublishBlogMediaAttachmentFile) : PublishBlogMediaAttachment
}

data class PublishBlogMediaAttachmentFile(
    val uri: String,
    val description: String?,
)
