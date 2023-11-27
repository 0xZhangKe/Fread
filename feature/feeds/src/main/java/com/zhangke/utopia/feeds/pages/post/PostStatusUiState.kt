package com.zhangke.utopia.feeds.pages.post

import android.net.Uri
import com.zhangke.utopia.status.account.LoggedAccount
import java.util.Locale

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
            val imageList = (attachment as? PostStatusAttachment.ImageAttachment)?.imageList ?: return 0
            return (maxMediaCount - imageList.size).coerceAtLeast(0)
        }
}

sealed interface PostStatusAttachment {

    class ImageAttachment(val imageList: List<PostStatusImage>) : PostStatusAttachment
}

data class PostStatusImage(
    val uri: Uri,
    val description: String?,
    /**
     * MB
     */
    val size: Float,
    /**
     * 0 to 1
     */
    val uploadJob: UploadingMediaJob,
)
