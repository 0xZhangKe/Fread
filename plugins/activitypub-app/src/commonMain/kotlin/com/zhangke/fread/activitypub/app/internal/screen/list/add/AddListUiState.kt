package com.zhangke.fread.activitypub.app.internal.screen.list.add

import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.fread.activitypub.app.internal.screen.list.edit.ListRepliesPolicy

data class AddListUiState(
    val name: TextFieldValue,
    val repliesPolicy: ListRepliesPolicy,
    val exclusive: Boolean,
    val showLoadingCover: Boolean,
    val accountList: List<ActivityPubAccountEntity>,
    val contentHasChanged: Boolean,
) {

    companion object {

        fun default(): AddListUiState {
            return AddListUiState(
                name = TextFieldValue(""),
                repliesPolicy = ListRepliesPolicy.LIST,
                exclusive = false,
                showLoadingCover = false,
                accountList = emptyList(),
                contentHasChanged = false,
            )
        }
    }
}
