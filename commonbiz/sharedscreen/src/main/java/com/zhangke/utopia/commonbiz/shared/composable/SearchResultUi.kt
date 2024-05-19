package com.zhangke.utopia.commonbiz.shared.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.common.status.model.SearchResultUiState
import com.zhangke.utopia.status.ui.BlogAuthorUi
import com.zhangke.utopia.status.ui.source.BlogPlatformUi
import com.zhangke.utopia.status.ui.ComposedStatusInteraction
import com.zhangke.utopia.status.ui.hashtag.HashtagUi

@Composable
fun SearchResultUi(
    searchResult: SearchResultUiState,
    modifier: Modifier = Modifier,
    indexInList: Int,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    when (searchResult) {
        is SearchResultUiState.Platform -> {
            BlogPlatformUi(
                modifier = modifier,
                platform = searchResult.platform,
            )
        }

        is SearchResultUiState.SearchedStatus -> {
            FeedsStatusNode(
                modifier = modifier,
                status = searchResult.status,
                indexInList = indexInList,
                composedStatusInteraction = composedStatusInteraction,
            )
        }

        is SearchResultUiState.SearchedHashtag -> {
            HashtagUi(
                modifier = modifier,
                tag = searchResult.hashtag,
                onClick = {
                    composedStatusInteraction.onHashtagClick(searchResult.role, it)
                },
            )
        }

        is SearchResultUiState.Author -> {
            BlogAuthorUi(
                modifier = modifier,
                author = searchResult.author,
                onClick = {
                    composedStatusInteraction.onUserInfoClick(searchResult.role, it)
                },
            )
        }
    }
}
