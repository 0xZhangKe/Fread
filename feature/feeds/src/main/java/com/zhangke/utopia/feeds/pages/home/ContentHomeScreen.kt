package com.zhangke.utopia.feeds.pages.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.voyager.LocalGlobalNavigator
import com.zhangke.utopia.feeds.pages.home.drawer.ContentHomeDrawer
import com.zhangke.utopia.feeds.pages.manager.selecttype.SelectContentTypeScreen
import kotlinx.coroutines.launch

class ContentHomeScreen : AndroidScreen() {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalGlobalNavigator.current
        val viewModel: ContentHomeViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val pagerState = rememberPagerState(pageCount = { uiState.contentConfigList.size })
        val coroutineScope = rememberCoroutineScope()
        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                ContentHomeDrawer(
                    contentConfigList = uiState.contentConfigList,
                    onContentConfigClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            pagerState.animateScrollToPage(uiState.contentConfigList.indexOf(it))
                        }
                    },
                    onAddContentClick = {
                        navigator.push(SelectContentTypeScreen())
                    },
                )
            },
        ) {
            Scaffold(
                topBar = {
                    Toolbar(
                        title = uiState.currentConfig?.configName.orEmpty(),
                        actions = {
                            SimpleIconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (pagerState.currentPage < (pagerState.pageCount - 1)) {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    }
                                },
                                imageVector = Icons.Default.ArrowRightAlt,
                                contentDescription = "Next Content"
                            )
                        }
                    )
                },
            ) { paddingValues ->
                val currentPage = pagerState.currentPage
                LaunchedEffect(currentPage) {
                    viewModel.onCurrentPageChange(currentPage)
                }
                LaunchedEffect(uiState.currentPageIndex) {
                    pagerState.animateScrollToPage(uiState.currentPageIndex)
                }
                HorizontalPager(
                    modifier = Modifier.padding(paddingValues),
                    state = pagerState,
                ) { pageIndex ->
                    val currentScreen =
                        viewModel.getContentScreen(uiState.contentConfigList[pageIndex])
                    if (currentScreen != null) {
                        Navigator(currentScreen)
                    }
                }
            }
        }
    }
}
