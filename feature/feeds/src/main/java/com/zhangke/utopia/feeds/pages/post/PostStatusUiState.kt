package com.zhangke.utopia.feeds.pages.post

import android.net.Uri
import com.zhangke.utopia.status.account.LoggedAccount
import java.util.Locale

data class PostStatusUiState(
    val account: LoggedAccount,
    val availableAccountList: List<LoggedAccount>,
    val content: String,
    val mediaList: List<Uri>,
    val maxMediaCount: Int,
    val sensitive: Boolean,
    val language: Locale,
) {

    val allowedSelectCount: Int get() = (maxMediaCount - mediaList.size).coerceAtLeast(0)
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
    val uploadProgress: Float,
)
