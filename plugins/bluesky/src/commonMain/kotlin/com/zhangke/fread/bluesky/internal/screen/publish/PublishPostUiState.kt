package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.ui.text.input.TextFieldValue
import app.bsky.graph.ListView
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.model.PostInteractionSetting
import com.zhangke.fread.bluesky.internal.model.ReplySetting
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.status.blog.Blog

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
    val replyBlog: Blog?,
    val quoteBlog: Blog?,
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
                replyBlog = null,
                quoteBlog = null,
                selectedLanguages = emptyList(),
                maxLanguageCount = 3,
                publishing = false,
                list = emptyList(),
            )
        }
    }
}

sealed interface PublishPostMediaAttachment {

    val medias: List<PublishPostMedia>
        get() = when (this) {
            is Image -> files
            is Video -> listOf(file)
        }

    data class Image(val files: List<PublishPostMediaAttachmentFile>) : PublishPostMediaAttachment

    data class Video(val file: PublishPostMediaAttachmentFile) : PublishPostMediaAttachment
}

data class PublishPostMediaAttachmentFile(
    val file: ContentProviderFile,
    override val isVideo: Boolean,
    override val alt: String?,
) : PublishPostMedia {

    override val uri: String
        get() = file.uri.toString()

}
