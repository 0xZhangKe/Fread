package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.post_status_scope_follower_only
import com.zhangke.fread.commonbiz.shared.screen.post_status_scope_mentioned_only
import com.zhangke.fread.commonbiz.shared.screen.post_status_scope_public
import com.zhangke.fread.commonbiz.shared.screen.post_status_scope_unlisted
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PublishBlogRules
import com.zhangke.fread.status.model.StatusVisibility
import org.jetbrains.compose.resources.StringResource

data class MultiAccountPublishingUiState(
    val accounts: List<LoggedAccount>,
    val publishing: Boolean,
    val content: TextFieldValue,
)

data class MultiPublishingAccountUiState(
    val account: LoggedAccount,
    val rules: PublishBlogRules,

)

sealed interface PublishingPostSetting{

    data class PostVisibility(
        val visibility: StatusVisibility,
    )

//    data class
}

internal val StatusVisibility.describeStringId: StringResource
    get() = when (this) {
        StatusVisibility.PUBLIC -> Res.string.post_status_scope_public
        StatusVisibility.UNLISTED -> Res.string.post_status_scope_unlisted
        StatusVisibility.PRIVATE -> Res.string.post_status_scope_follower_only
        StatusVisibility.DIRECT -> Res.string.post_status_scope_mentioned_only
    }
