package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PublishBlogRules

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

    )
}