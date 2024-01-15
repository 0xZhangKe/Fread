package com.zhangke.utopia.feeds.pages.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.voyager.LocalGlobalNavigator
import com.zhangke.utopia.feeds.pages.home.drawer.ContentHomeDrawer
import com.zhangke.utopia.feeds.pages.manager.selecttype.SelectContentTypeScreen
import kotlinx.coroutines.launch

class ContentHomeScreen : Screen {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalGlobalNavigator.current
        val viewModel: ContentHomeViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val pagerState = rememberPagerState(pageCount = { uiState.contentConfigList.size })
        val coroutineScope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
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
                }
            },
        ) {
            val scrollBehavior =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        navigationIcon = {
                            SimpleIconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.open()
                                    }
                                },
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                            )
                        },
                        scrollBehavior = scrollBehavior,
                        title = {
                            Text(
                                text = uiState.currentConfig?.configName.orEmpty(),
                                fontSize = 18.sp,
                            )
                        },
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
                        },
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
