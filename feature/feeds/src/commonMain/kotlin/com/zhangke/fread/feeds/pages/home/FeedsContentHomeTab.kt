package com.zhangke.fread.feeds.pages.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.feeds.pages.manager.add.type.SelectContentTypeScreenNavKey
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedsContentHomeScreen() {
    val backStack = LocalNavBackStack.currentOrThrow
    val coroutineScope = rememberCoroutineScope()
    val viewModel: ContentHomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    if (uiState.contentAndTabList.isEmpty()) {
        if (uiState.loading) {
            Box(modifier = Modifier.fillMaxSize())
        } else {
            EmptyContent(modifier = Modifier.fillMaxSize()) {
                backStack.add(SelectContentTypeScreenNavKey)
            }
        }
    } else {
        val mainTabConnection = LocalNestedTabConnection.current
        val pagerState = rememberPagerState(
            initialPage = uiState.currentPageIndex.coerceAtLeast(0),
            pageCount = { uiState.contentAndTabList.size },
        )
        ConsumeFlow(mainTabConnection.switchToNextTabFlow) {
            if (pagerState.currentPage < pagerState.pageCount - 1) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        }
        ConsumeFlow(mainTabConnection.scrollToContentTabFlow) { content ->
            val index = uiState.contentAndTabList.indexOfFirst { it.first == content }
            if (index in 0 until pagerState.pageCount) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        }
        ConsumeFlow(viewModel.switchPageFlow) {
            coroutineScope.launch {
                viewModel.onSwitchPageFlowUsed()
                pagerState.animateScrollToPage(it)
            }
        }
        val targetPage = pagerState.targetPage
        LaunchedEffect(targetPage) {
            viewModel.onCurrentPageChanged(targetPage)
        }
        val contentScrollInProgress by mainTabConnection.contentScrollInpProgress.collectAsState()
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = !contentScrollInProgress,
        ) { pageIndex ->
            val currentScreen = uiState.contentAndTabList[pageIndex].second
            with(currentScreen) {
                Content()
            }
        }
    }
}
