package com.zhangke.fread.activitypub.app.internal.screen.user.search

import com.zhangke.activitypub.entities.ActivityPubAccountEntity

data class SearchUserUiState(
    val query: String,
    val searching: Boolean,
    val accounts: List<ActivityPubAccountEntity>
) {

    companion object {

        fun default(): SearchUserUiState {
            return SearchUserUiState(
                query = "",
                searching = false,
                accounts = emptyList(),
            )
        }
    }
}
