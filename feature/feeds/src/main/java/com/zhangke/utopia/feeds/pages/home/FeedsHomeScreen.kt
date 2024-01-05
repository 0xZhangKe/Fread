package com.zhangke.utopia.feeds.pages.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.topout.TopOutTopBarLayout
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.framework.voyager.tryPush
import com.zhangke.utopia.commonbiz.shared.screen.PostStatusIntermediaryScreen
import com.zhangke.utopia.feeds.pages.home.feeds.FeedsTab
import com.zhangke.utopia.feeds.pages.home.manager.AllFeedsManagerScreen
import com.zhangke.utopia.feeds.pages.manager.add.AddFeedsManagerScreen

class FeedsHomeScreen : AndroidScreen() {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel: FeedsHomeViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = rememberSnackbarHostState()
        ConsumeSnackbarFlow(
            hostState = snackbarHostState,
            messageTextFlow = viewModel.errorMessageFlow
        )
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navigator.push(PostStatusIntermediaryScreen())
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                    )
                }
            },
        ) { paddings ->
            ModalDrawer(
                modifier = Modifier.padding(paddings),
                drawerContent = {

                },
            ) {

            }
            LoadableLayout(
                modifier = Modifier.padding(paddings),
                state = uiState,
            ) { realUiState ->
                if (realUiState.feedsConfigList.isNotEmpty()) {
                    TopOutTopBarLayout(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            val platformList =
                                realUiState.feedsConfigList[realUiState.selectedIndex].platformList
                            FeedsHomeTopBar(
                                modifier = Modifier.fillMaxWidth(),
                                selectedIndex = realUiState.selectedIndex,
                                feedsConfigList = realUiState.feedsConfigList.map { it.feedsConfig },
                                platformList = platformList,
                                onTabClick = viewModel::onTabSelected,
                                onMenuClick = {
                                    bottomSheetNavigator.show(
                                        AllFeedsManagerScreen(
                                            feedsConfigWithPlatformList = realUiState.feedsConfigList,
                                            onItemClick = viewModel::onTabSelected,
                                        )
                                    )
                                },
                                onServerItemClick = {
                                    viewModel.screenProvider
                                        .getPlatformDetailScreen(it.uri)
                                        ?.let(navigator::tryPush)
                                },
                            )
                        },
                    ) {
                        val pagerState = rememberPagerState(
                            pageCount = realUiState.feedsConfigList::size,
                        )
                        val pageIndexOfPager = pagerState.currentPage
                        LaunchedEffect(pageIndexOfPager) {
                            viewModel.onTabSelected(pageIndexOfPager)
                        }
                        LaunchedEffect(realUiState.selectedIndex) {
                            pagerState.scrollToPage(realUiState.selectedIndex)
                        }

                        HorizontalPager(
                            modifier = Modifier
                                .fillMaxWidth(),
                            state = pagerState,
                        ) { index ->
                            FeedsTab(
                                feedsConfig = realUiState.feedsConfigList[index].feedsConfig,
                                showSnakeMessage = viewModel::showErrorMessage,
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Button(
                                modifier = Modifier.align(Alignment.Center),
                                onClick = {
                                    navigator.push(AddFeedsManagerScreen())
                                },
                            ) {
                                Text(text = "Add Feeds")
                            }
                        }
                    }
                }
            }
        }
    }
}
