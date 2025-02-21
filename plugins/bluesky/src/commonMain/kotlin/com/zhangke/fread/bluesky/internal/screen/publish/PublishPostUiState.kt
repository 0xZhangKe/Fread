package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.ui.text.input.TextFieldValue
import app.bsky.graph.ListView
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.model.PostInteractionSetting
import com.zhangke.fread.bluesky.internal.model.ReplySetting

data class PublishPostUiState(
    val content: TextFieldValue,
    val maxCharacters: Int,
    val maxMediaCount: Int,
    val account: BlueskyLoggedAccount?,
    val attachment: PublishPostMediaAttachment?,
    val interactionSetting: PostInteractionSetting,
    val list: List<ListView>,
) {

    companion object {

        fun default(): PublishPostUiState {
            return PublishPostUiState(
                content = TextFieldValue(),
                maxCharacters = 300,
                maxMediaCount = 4,
                interactionSetting = PostInteractionSetting(
                    allowQuote = true,
                    replySetting = ReplySetting.Everybody,
                ),
                account = null,
                attachment = null,
                list = emptyList(),
            )
        }
    }
}

sealed interface PublishPostMediaAttachment {

    data class Image(val files: List<PublishPostMediaAttachmentFile>) : PublishPostMediaAttachment

    data class Video(val file: PublishPostMediaAttachmentFile) : PublishPostMediaAttachment
}

data class PublishPostMediaAttachmentFile(
    val uri: String,
    val alt: String?,
)
