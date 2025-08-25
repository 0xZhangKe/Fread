package com.zhangke.fread.feeds.pages.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.feeds.pages.manager.add.type.SelectContentTypeScreen
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import kotlinx.coroutines.launch

class FeedsContentHomeScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        CompositionLocalProvider(LocalNavigator provides navigator) {
            val coroutineScope = rememberCoroutineScope()
            val viewModel: ContentHomeViewModel = getViewModel()
            val uiState by viewModel.uiState.collectAsState()
            if (uiState.contentAndTabList.isEmpty()) {
                if (uiState.loading) {
                    Box(modifier = Modifier.Companion.fillMaxSize())
                } else {
                    EmptyContent(modifier = Modifier.Companion.fillMaxSize()) {
                        navigator.push(SelectContentTypeScreen())
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
                        TabContent(this@FeedsContentHomeScreen, null)
                    }
                }
            }
        }
    }
}
