package com.zhangke.fread.activitypub.app.internal.screen.list

import com.zhangke.activitypub.entities.ActivityPubListEntity

data class CreatedListsUiState(
    val lists: List<ActivityPubListEntity>,
    val loading: Boolean,
    val pageError: Throwable?,
) {

    companion object {

        fun default(
            loading: Boolean = false,
        ): CreatedListsUiState {
            return CreatedListsUiState(
                loading = loading,
                pageError = null,
                lists = emptyList(),
            )
        }
    }
}
