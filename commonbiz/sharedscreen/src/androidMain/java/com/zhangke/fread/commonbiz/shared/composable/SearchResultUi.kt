package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.status.model.SearchResultUiState
import com.zhangke.fread.status.ui.BlogAuthorUi
import com.zhangke.fread.status.ui.source.BlogPlatformUi
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.hashtag.HashtagUi

@Composable
fun SearchResultUi(
    searchResult: SearchResultUiState,
    modifier: Modifier = Modifier,
    indexInList: Int,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val context = LocalContext.current
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
                onUrlClick = {
                    BrowserLauncher.launchWebTabInApp(context, it, searchResult.role)
                }
            )
        }
    }
}
