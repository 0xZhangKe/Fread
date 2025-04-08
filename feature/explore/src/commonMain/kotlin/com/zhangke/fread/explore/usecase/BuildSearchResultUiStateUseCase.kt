package com.zhangke.fread.explore.usecase

import com.zhangke.fread.common.status.model.SearchResultUiState
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.search.SearchResult
import me.tatarka.inject.annotations.Inject

class BuildSearchResultUiStateUseCase @Inject constructor() {

    suspend operator fun invoke(role: IdentityRole, result: SearchResult): SearchResultUiState {
        return when (result) {
            is SearchResult.Author -> {
                SearchResultUiState.Author(role, result.user)
            }

            is SearchResult.Platform -> {
                SearchResultUiState.Platform(role, result.platform)
            }

            is SearchResult.SearchedStatus -> {
                SearchResultUiState.SearchedStatus(result.status)
            }

            is SearchResult.SearchedHashtag -> {
                SearchResultUiState.SearchedHashtag(role, result.hashtag)
            }
        }
    }
}
