package com.zhangke.utopia.feeds.pages.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.utopia.feeds.pages.manager.add.pre.PreAddFeedsScreen
import com.zhangke.utopia.status.ui.common.LocalMainTabConnection

class ContentHomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        CompositionLocalProvider(
            LocalNavigator provides navigator
        ) {
            val viewModel: ContentHomeViewModel = getViewModel()
            val uiState by viewModel.uiState.collectAsState()
            if (uiState.contentConfigList.isEmpty()) {
                EmptyContent(modifier = Modifier.fillMaxSize()) {
                    navigator.push(PreAddFeedsScreen())
                }
            } else {
                val mainTabConnection = LocalMainTabConnection.current
                val pagerState = rememberPagerState(pageCount = { uiState.contentConfigList.size })
                ConsumeFlow(mainTabConnection.switchToNextTabFlow) {
                    if (pagerState.currentPage < pagerState.pageCount - 1) {
                        viewModel.onCurrentPageChange(pagerState.currentPage + 1)
                    }
                }
                ConsumeFlow(mainTabConnection.scrollToContentTabFlow) {
                    val index = uiState.contentConfigList.indexOf(it)
                    if (index in 0 until pagerState.pageCount) {
                        viewModel.onCurrentPageChange(index)
                    }
                }
                val targetPage = pagerState.targetPage
                LaunchedEffect(targetPage) {
                    viewModel.onCurrentPageChange(targetPage)
                }
                LaunchedEffect(uiState.currentPageIndex) {
                    pagerState.animateScrollToPage(uiState.currentPageIndex)
                }
                HorizontalPager(
                    state = pagerState,
                ) { pageIndex ->
                    val currentScreen = remember(uiState.contentConfigList, pageIndex) {
                        viewModel.getContentScreen(
                            contentConfig = uiState.contentConfigList[pageIndex],
                            isLatestTab = pageIndex == uiState.contentConfigList.lastIndex,
                        )
                    }
                    if (currentScreen == null) {
                        Text(text = "Error! can't find any tab fro this config!")
                    } else {
                        with(currentScreen) {
                            TabContent(null)
                        }
                    }
                }
            }
        }
    }
}
