package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.status.account.LoggedAccount

data class PublishPostUiState(
    val content: TextFieldValue,
    val account: BlueskyLoggedAccount?,
    val attachment: PublishPostMediaAttachment?,
)

sealed interface PublishPostMediaAttachment {

    data class Image(val files: List<PublishPostMediaAttachmentFile>) : PublishPostMediaAttachment

    data class Video(val file: PublishPostMediaAttachmentFile) : PublishPostMediaAttachment
}

data class PublishPostMediaAttachmentFile(
    val uri: String,
    val alt: String?,
)
