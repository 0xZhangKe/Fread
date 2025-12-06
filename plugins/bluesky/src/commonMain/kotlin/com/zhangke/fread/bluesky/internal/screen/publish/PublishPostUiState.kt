package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import app.bsky.actor.ProfileView
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.PostInteractionSetting
import com.zhangke.fread.status.model.ReplySetting
import com.zhangke.fread.status.model.StatusList

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
    val list: List<StatusList>,
    val mentionState: LoadableState<List<ProfileView>>,
) {

    val remainingImageCount: Int
        get() {
            return if (attachment is PublishPostMediaAttachment.Image) {
                maxMediaCount - attachment.files.size
            } else {
                maxMediaCount
            }
        }

    val showAddAccountIcon: Boolean
        get() = replyBlog == null

    val hasInputtedData: Boolean
        get() {
            if (content.text.isNotEmpty()) return true
            if (attachment != null) return true
            return false
        }

    companion object {

        fun default(
            defaultContent: String? = null,
        ): PublishPostUiState {
            return PublishPostUiState(
                content = TextFieldValue(
                    text = defaultContent.orEmpty(),
                    selection = TextRange(defaultContent?.length ?: 0),
                ),
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
                mentionState = LoadableState.idle(),
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
