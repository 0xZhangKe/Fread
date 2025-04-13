package com.zhangke.fread.activitypub.app.internal.screen.list.edit

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubListEntity

data class EditListUiState(
    val accountsLoading: Boolean,
    val loadAccountsError: Throwable?,
    val name: String,
    val repliesPolicy: ListRepliesPolicy,
    val exclusive: Boolean,
    val showLoadingCover: Boolean,
    val accountList: List<ActivityPubAccountEntity>,
) {

    companion object {

        fun default(list: ActivityPubListEntity): EditListUiState {
            return EditListUiState(
                accountsLoading = false,
                loadAccountsError = null,
                name = list.title,
                repliesPolicy = ListRepliesPolicy.fromName(list.repliesPolicy),
                exclusive = list.exclusive,
                showLoadingCover = false,
                accountList = emptyList(),
            )
        }
    }
}

enum class ListRepliesPolicy {

    FOLLOWING,
    LIST,
    NONE;

    companion object {

        fun fromName(name: String): ListRepliesPolicy {
            return when (name) {
                "followed" -> FOLLOWING
                "list" -> LIST
                "none" -> NONE
                else -> FOLLOWING
            }
        }
    }
}
