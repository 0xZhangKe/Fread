package com.zhangke.fread.activitypub.app.internal.screen.list.edit

import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubListEntity

data class EditListUiState(
    val accountsLoading: Boolean,
    val loadAccountsError: Throwable?,
    val name: TextFieldValue,
    val repliesPolicy: ListRepliesPolicy,
    val exclusive: Boolean,
    val showLoadingCover: Boolean,
    val accountList: List<ActivityPubAccountEntity>,
    val contentHasChanged: Boolean,
) {

    companion object {

        fun default(list: ActivityPubListEntity): EditListUiState {
            return EditListUiState(
                accountsLoading = false,
                loadAccountsError = null,
                name = TextFieldValue(list.title),
                repliesPolicy = ListRepliesPolicy.fromName(list.repliesPolicy),
                exclusive = list.exclusive,
                showLoadingCover = false,
                accountList = emptyList(),
                contentHasChanged = false,
            )
        }
    }
}

enum class ListRepliesPolicy(val apiName: String) {

    FOLLOWING("followed"),
    LIST("list"),
    NONE("none");

    companion object {

        fun fromName(name: String): ListRepliesPolicy {
            return entries.first { it.apiName == name }
        }
    }
}
