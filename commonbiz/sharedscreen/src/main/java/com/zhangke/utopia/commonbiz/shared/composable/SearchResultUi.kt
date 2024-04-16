package com.zhangke.utopia.commonbiz.shared.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.common.status.model.SearchResultUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.BlogAuthorUi
import com.zhangke.utopia.status.ui.BlogPlatformUi
import com.zhangke.utopia.status.ui.hashtag.HashtagUi

@Composable
fun SearchResultUi(
    searchResult: SearchResultUiState,
    role: IdentityRole,
    modifier: Modifier = Modifier,
    indexInList: Int,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onInteractive: (Status, StatusUiInteraction) -> Unit,
    onHashtagClick: (Hashtag) -> Unit,
    onVoted: (Status, List<BlogPoll.Option>) -> Unit,
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
                role = role,
                status = searchResult.status,
                indexInList = indexInList,
                onUserInfoClick = onUserInfoClick,
                onInteractive = onInteractive,
                onVoted = onVoted,
            )
        }

        is SearchResultUiState.SearchedHashtag -> {
            HashtagUi(
                modifier = modifier,
                tag = searchResult.hashtag,
                onClick = onHashtagClick,
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
