package com.zhangke.utopia.explore.usecase

import com.zhangke.utopia.common.status.model.SearchResultUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.search.SearchResult
import javax.inject.Inject

class BuildSearchResultUiStateUseCase @Inject constructor(
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) {

    operator fun invoke(role: IdentityRole, result: SearchResult): SearchResultUiState {
        return when (result) {
            is SearchResult.Author -> {
                SearchResultUiState.Author(role, result.user)
            }

            is SearchResult.Platform -> {
                SearchResultUiState.Platform(role, result.platform)
            }

            is SearchResult.SearchedStatus -> {
                SearchResultUiState.SearchedStatus(
                    buildStatusUiState(role, result.status)
                )
            }

            is SearchResult.SearchedHashtag -> {
                SearchResultUiState.SearchedHashtag(role, result.hashtag)
            }
        }
    }
}
