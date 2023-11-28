package com.zhangke.utopia.feeds.pages.post

import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.utopia.status.account.LoggedAccount
import java.util.Locale
import kotlin.time.Duration

data class PostStatusUiState(
    val account: LoggedAccount,
    val availableAccountList: List<LoggedAccount>,
    val content: String,
    val attachment: PostStatusAttachment?,
    val maxMediaCount: Int,
    val sensitive: Boolean,
    val language: Locale,
) {

    val allowedSelectCount: Int
        get() {
            val imageList = attachment?.asImageAttachment?.imageList ?: return maxMediaCount
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
    ): PostStatusAttachment

    val asImageAttachment: ImageAttachment? get() = this as? ImageAttachment

    val asVideoAttachment: VideoAttachment? get() = this as? VideoAttachment
}

data class PostStatusFile(
    val file: ContentProviderFile,
    val description: String?,
    val uploadJob: UploadMediaJob,
)
