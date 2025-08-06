package com.zhangke.fread.feeds.pages.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.feeds.Res
import com.zhangke.fread.feeds.ic_home
import com.zhangke.fread.feeds.pages.manager.add.pre.PreAddFeedsScreen
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import org.jetbrains.compose.resources.painterResource

class FeedsHomeTab : PagerTab {

    override val options: PagerTabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_home)
            return remember {
                PagerTabOptions(
                    title = "Home",
                    icon = icon,
                )
            }
        }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?,
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ContentHomeViewModel = screen.getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        if (uiState.contentConfigList.isEmpty()) {
            if (uiState.loading) {
                Box(modifier = Modifier.Companion.fillMaxSize())
            } else {
                EmptyContent(modifier = Modifier.Companion.fillMaxSize()) {
                    navigator.push(PreAddFeedsScreen())
                }
            }
        } else {
            val mainTabConnection = LocalNestedTabConnection.current
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
            val contentScrollInProgress by
            mainTabConnection.contentScrollInpProgress.collectAsState()
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = !contentScrollInProgress,
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
                        TabContent(screen, null)
                    }
                }
            }
        }
    }
}
