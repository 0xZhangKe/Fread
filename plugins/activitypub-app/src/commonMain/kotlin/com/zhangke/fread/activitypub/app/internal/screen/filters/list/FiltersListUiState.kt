package com.zhangke.fread.activitypub.app.internal.screen.filters.list

import com.zhangke.framework.composable.TextString

data class FiltersListUiState(
    val initializing: Boolean,
    val list: List<FilterItemUiState>,
) {

    companion object {

        fun default(): FiltersListUiState {
            return FiltersListUiState(
                initializing = false,
                list = emptyList(),
            )
        }
    }
}

data class FilterItemUiState(
    val id: String,
    val title: String,
    val validateDescription: TextString,
)
