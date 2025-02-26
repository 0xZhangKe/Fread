package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.input.TextFieldValue
import app.bsky.graph.ListView
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.model.PostInteractionSetting
import com.zhangke.fread.bluesky.internal.model.ReplySetting

data class PublishPostUiState(
    val content: TextFieldValue,
    val maxCharacters: Int,
    val maxMediaCount: Int,
    val mediaAltMaxCharacters: Int,
    val account: BlueskyLoggedAccount?,
    val attachment: PublishPostMediaAttachment?,
    val interactionSetting: PostInteractionSetting,
    val selectedLanguages: List<String>,
    val maxLanguageCount: Int,
    val publishing: Boolean,
    val list: List<ListView>,
) {

    val remainingImageCount: Int
        get() {
            return if (attachment is PublishPostMediaAttachment.Image) {
                maxMediaCount - attachment.files.size
            } else {
                maxMediaCount
            }
        }

    val remainingTextCount: Int get() = maxCharacters - content.text.length

    companion object {

        fun default(): PublishPostUiState {
            return PublishPostUiState(
                content = TextFieldValue(),
                maxCharacters = 300,
                mediaAltMaxCharacters = 2000,
                maxMediaCount = 4,
                interactionSetting = PostInteractionSetting(
                    allowQuote = true,
                    replySetting = ReplySetting.Everybody,
                ),
                account = null,
                attachment = null,
                selectedLanguages = emptyList(),
                maxLanguageCount = 3,
                publishing = false,
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
    val file: ContentProviderFile,
    val alt: String?,
)
