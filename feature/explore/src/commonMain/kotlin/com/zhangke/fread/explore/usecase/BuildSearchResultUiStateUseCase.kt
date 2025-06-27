package com.zhangke.fread.explore.usecase

import com.zhangke.fread.common.status.model.SearchResultUiState
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.search.SearchResult
import me.tatarka.inject.annotations.Inject

class BuildSearchResultUiStateUseCase @Inject constructor() {

    suspend operator fun invoke(locator: PlatformLocator, result: SearchResult): SearchResultUiState {
        return when (result) {
            is SearchResult.Author -> {
                SearchResultUiState.Author(locator, result.user)
            }

            is SearchResult.Platform -> {
                SearchResultUiState.Platform(locator, result.platform)
            }

            is SearchResult.SearchedStatus -> {
                SearchResultUiState.SearchedStatus(result.status)
            }

            is SearchResult.SearchedHashtag -> {
                SearchResultUiState.SearchedHashtag(locator, result.hashtag)
            }
        }
    }
}
