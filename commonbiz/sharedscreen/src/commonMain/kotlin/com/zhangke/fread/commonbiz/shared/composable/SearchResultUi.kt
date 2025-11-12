package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.browser.launchWebTabInApp
import com.zhangke.fread.common.status.model.SearchResultUiState
import com.zhangke.fread.status.ui.BlogAuthorUi
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.hashtag.HashtagUi
import com.zhangke.fread.status.ui.source.BlogPlatformUi

@Composable
fun SearchResultUi(
    searchResult: SearchResultUiState,
    modifier: Modifier = Modifier,
    indexInList: Int,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    val coroutineScope = rememberCoroutineScope()
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
                    composedStatusInteraction.onHashtagClick(searchResult.locator, it)
                },
            )
        }

        is SearchResultUiState.Author -> {
            BlogAuthorUi(
                modifier = modifier,
                author = searchResult.author,
                onClick = {
                    composedStatusInteraction.onUserInfoClick(searchResult.locator, it)
                },
                onUrlClick = {
                    browserLauncher.launchWebTabInApp(coroutineScope, it, searchResult.locator)
                }
            )
        }
    }
}
