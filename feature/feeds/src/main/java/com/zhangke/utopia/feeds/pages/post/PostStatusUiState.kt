package com.zhangke.utopia.feeds.pages.post

import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.emoji.CustomEmoji
import com.zhangke.utopia.status.ui.emoji.CustomEmojiCell
import java.util.Locale
import kotlin.time.Duration

data class PostStatusUiState(
    val account: LoggedAccount,
    val availableAccountList: List<LoggedAccount>,
    val content: String,
    val attachment: PostStatusAttachment?,
    val maxMediaCount: Int,
    val visibility: PostStatusVisibility,
    val sensitive: Boolean,
    val warningContent: String,
    val emojiList: List<CustomEmojiCell>,
    val language: Locale,
) {

    val allowedSelectCount: Int
        get() {
            val imageList = attachment?.asImageAttachmentOrNull?.imageList ?: return maxMediaCount
            return (maxMediaCount - imageList.size).coerceAtLeast(0)
        }
}

sealed interface PostStatusAttachment {

    data class ImageAttachment(val imageList: List<PostStatusFile>) : PostStatusAttachment

    data class VideoAttachment(val video: PostStatusFile) : PostStatusAttachment

    data class Poll(
        val optionList: List<String>,
        val multiple: Boolean,
        val duration: Duration,
    ) : PostStatusAttachment

    val asImageAttachmentOrNull: ImageAttachment? get() = this as? ImageAttachment

    val asVideoAttachmentOrNull: VideoAttachment? get() = this as? VideoAttachment

    val asPollAttachment: Poll get() = this as Poll
}

data class PostStatusFile(
    val file: ContentProviderFile,
    val description: String?,
    val uploadJob: UploadMediaJob,
)

enum class PostStatusVisibility {

    PUBLIC,
    FOLLOWERS_ONLY,
    MENTIONS_ONLY;

    val describeStringId: Int
        get() = when (this) {
            PUBLIC -> R.string.post_status_scope_public
            FOLLOWERS_ONLY -> R.string.post_status_scope_follower_only
            MENTIONS_ONLY -> R.string.post_status_scope_mentioned_only
        }
}
