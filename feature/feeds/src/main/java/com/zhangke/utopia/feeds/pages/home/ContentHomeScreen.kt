package com.zhangke.utopia.feeds.pages.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.utopia.feeds.pages.home.drawer.ContentHomeDrawer
import com.zhangke.utopia.feeds.pages.manager.selecttype.SelectContentTypeScreen
import kotlinx.coroutines.launch

class ContentHomeScreen : Screen {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel: ContentHomeViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()

        ConsumeFlow(viewModel.openScreenFlow) { route ->
            navigator.push(route)
        }
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    ContentHomeDrawer(
                        contentConfigList = uiState.contentConfigList,
                        onContentConfigClick = {
                            coroutineScope.launch {
                                drawerState.close()
                                viewModel.switchPageIndex(uiState.contentConfigList.indexOf(it))
                            }
                        },
                        onAddContentClick = {
                            navigator.push(SelectContentTypeScreen())
                        },
                        onMove = viewModel::onContentConfigMove,
                        onEditClick = viewModel::onContentConfigEditClick,
                    )
                }
            },
        ) {
            val scrollBehavior =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            val snackbarHostState = rememberSnackbarHostState()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
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
                                modifier = Modifier.clickable {
                                    uiState.currentConfig?.let(viewModel::onConfigTitleClick)
                                },
                                text = uiState.currentConfig?.configName.orEmpty(),
                                fontSize = 18.sp,
                            )
                        },
                        actions = {
                            SimpleIconButton(
                                onClick = {
                                    viewModel.switchPageIndex(uiState.currentPageIndex + 1)
                                },
                                imageVector = Icons.Default.ArrowRightAlt,
                                contentDescription = "Next Content"
                            )
                        },
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = viewModel::onPostStatusClick,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                        )
                    }
                }
            ) { paddingValues ->
                if (uiState.contentConfigList.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(text = "Please add content first")
                        Box(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            navigator.push(SelectContentTypeScreen())
                        }) {
                            Text(text = "Add Content")
                        }
                    }
                } else {
                    val pagerState =
                        rememberPagerState(pageCount = { uiState.contentConfigList.size })
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
                        CompositionLocalProvider(
                            LocalSnackbarHostState provides snackbarHostState,
                        ) {
                            if (currentScreen == null) {
                                Text(text = "Error! can't find any tab fro this config!")
                            } else {
                                with(currentScreen) {
                                    TabContent()
                                }
                            }
                        }
                    }
                }
            }
        }
        ConsumeFlow(viewModel.openSelectAccountForPostFlow) {
            val screen = SelectAccountForPostStatusScreen(it) { account ->
                viewModel.onPostStatusAccountClick(account)
            }
            bottomSheetNavigator.show(screen)
        }
    }
}
