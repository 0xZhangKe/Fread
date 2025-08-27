package com.zhangke.fread.activitypub.app.internal.screen.add.select

import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.PlatformSnapshot

data class SelectPlatformUiState(
    val query: String,
    val platformSnapshotList: List<SearchPlatformResult>,
    val querying: Boolean,
    val searchedResult: List<SearchPlatformResult>,
    val loadingPlatformForAdd: Boolean,
) {

    companion object {

        fun default(): SelectPlatformUiState {
            return SelectPlatformUiState(
                query = "",
                platformSnapshotList = emptyList(),
                querying = false,
                searchedResult = emptyList(),
                loadingPlatformForAdd = false,
            )
        }
    }
}

sealed interface SearchPlatformResult {

    data class SearchedSnapshot(val snapshot: PlatformSnapshot) : SearchPlatformResult

    data class SearchedPlatform(val platform: BlogPlatform) : SearchPlatformResult
}
