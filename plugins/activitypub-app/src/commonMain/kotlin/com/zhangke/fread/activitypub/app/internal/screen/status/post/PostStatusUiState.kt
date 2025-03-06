package com.zhangke.fread.activitypub.app.internal.screen.status.post

import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.Locale
import com.zhangke.framework.utils.getDefaultLocale
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.GroupedCustomEmojiCell
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.StatusVisibility
import kotlin.time.Duration

data class PostStatusUiState(
    val account: ActivityPubLoggedAccount,
    val availableAccountList: List<ActivityPubLoggedAccount>,
    val accountChangeable: Boolean,
    val content: TextFieldValue,
    val attachment: PostStatusAttachment?,
    val visibility: StatusVisibility,
    val visibilityChangeable: Boolean,
    val sensitive: Boolean,
    val warningContent: TextFieldValue,
    val replyToBlog: Blog?,
    val emojiList: List<GroupedCustomEmojiCell>,
    val language: Locale,
    val rules: PostBlogRules,
    val publishing: Boolean,
    val mentionState: LoadableState<List<ActivityPubAccountEntity>>,
) {

    val allowedSelectCount: Int
        get() {
            val imageList =
                attachment?.asImageOrNull?.imageList ?: return rules.maxMediaCount
            return (rules.maxMediaCount - imageList.size).coerceAtLeast(0)
        }

    val allowedInputCount: Int get() = rules.maxCharacters - content.text.length

    fun hasInputtedData(): Boolean {
        if (content.text.isNotEmpty()) return true
        if (attachment != null) return true
        if (sensitive && warningContent.text.isNotEmpty()) return true
        return false
    }

    companion object {

        fun default(
            account: ActivityPubLoggedAccount,
            allLoggedAccount: List<ActivityPubLoggedAccount>,
            visibility: StatusVisibility,
            replyToAuthorInfo: PostStatusScreenParams.ReplyStatusParams?,
            content: TextFieldValue = TextFieldValue(""),
            sensitive: Boolean = false,
            warningContent: TextFieldValue = TextFieldValue(""),
            language: Locale? = null,
            accountChangeable: Boolean = true,
            visibilityChangeable: Boolean = true,
            attachment: PostStatusAttachment? = null,
        ): PostStatusUiState {
            return PostStatusUiState(
                account = account,
                availableAccountList = allLoggedAccount,
                content = content,
                attachment = attachment,
                visibility = visibility,
                sensitive = sensitive,
                replyToBlog = replyToAuthorInfo?.replyingToBlog,
                warningContent = warningContent,
                emojiList = emptyList(),
                language = language ?: getDefaultLocale(),
                rules = PostBlogRules.default(),
                accountChangeable = accountChangeable,
                visibilityChangeable = visibilityChangeable,
                publishing = false,
                mentionState = LoadableState.idle(),
            )
        }
    }
}

sealed interface PostStatusAttachment {

    data class Image(val imageList: List<PostStatusMediaAttachmentFile>) : PostStatusAttachment

    data class Video(val video: PostStatusMediaAttachmentFile) : PostStatusAttachment

    data class Poll(
        val optionList: List<String>,
        val multiple: Boolean,
        val duration: Duration,
    ) : PostStatusAttachment

    val asImageOrNull: Image? get() = this as? Image

    val asVideoOrNull: Video? get() = this as? Video

    val asPollAttachment: Poll get() = this as Poll

    val asPollAttachmentOrNull: Poll? get() = this as? Poll
}

sealed interface PostStatusMediaAttachmentFile {

    val previewUri: String

    val description: String?

    val fileId: String?
        get() = when (this) {
            is LocalFile -> {
                uploadJob.uploadState.value.successIdOrNull
            }

            is RemoteFile -> {
                id
            }
        }

    data class LocalFile(
        val file: ContentProviderFile,
        override val description: String?,
        val uploadJob: UploadMediaJob,
    ) : PostStatusMediaAttachmentFile {

        val isVideo: Boolean
            get() = file.isVideo

        override val previewUri: String
            get() = file.uri.toString()
    }

    data class RemoteFile(
        val id: String,
        val url: String,
        override val description: String?,
    ) : PostStatusMediaAttachmentFile {

        override val previewUri: String
            get() = url
    }
}

data class PostBlogRules(
    val maxCharacters: Int,
    val maxMediaCount: Int,
    val maxPollOptions: Int,
) {
    companion object {

        fun default(): PostBlogRules {
            return PostBlogRules(
                maxCharacters = 1000,
                maxMediaCount = 4,
                maxPollOptions = 4,
            )
        }
    }
}
