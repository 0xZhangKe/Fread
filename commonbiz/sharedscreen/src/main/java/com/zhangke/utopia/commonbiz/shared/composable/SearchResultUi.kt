package com.zhangke.utopia.commonbiz.shared.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.common.status.model.SearchResultUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.BlogAuthorUi
import com.zhangke.utopia.status.ui.BlogPlatformUi
import com.zhangke.utopia.status.ui.hashtag.HashtagUi

@Composable
fun SearchResultUi(
    searchResult: SearchResultUiState,
    modifier: Modifier = Modifier,
    indexInList: Int,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onInteractive: (Status, StatusUiInteraction) -> Unit,
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
                onUserInfoClick = onUserInfoClick,
                onInteractive = onInteractive,
            )
        }

        is SearchResultUiState.SearchedHashtag -> {
            HashtagUi(
                modifier = modifier,
                tag = searchResult.hashtag,
            )
        }

        is SearchResultUiState.Author -> {
            BlogAuthorUi(
                modifier = modifier,
                author = searchResult.author,
                onClick = onUserInfoClick,
            )
        }
    }
}
