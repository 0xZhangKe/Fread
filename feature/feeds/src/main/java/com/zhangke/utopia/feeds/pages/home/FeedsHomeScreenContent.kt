package com.zhangke.utopia.feeds.pages.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.TextWithIcon
import com.zhangke.framework.composable.UtopiaTabRow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.successDataOrNull
import com.zhangke.framework.composable.theme.TopAppBarDefault
import com.zhangke.framework.composable.topout.TopOutTopBarLayout
import com.zhangke.utopia.feeds.pages.home.feeds.FeedsScreenUiState
import com.zhangke.utopia.status.platform.BlogPlatform

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//internal fun FeedsHomeScreenContent(
//    uiState: FeedsHomeUiState,
//    onTabSelected: (Int) -> Unit,
//    onAddFeedsClick: () -> Unit,
//    onRefresh: () -> Unit,
//    onLoadMore: () -> Unit,
//    onPostStatusClick: () -> Unit,
//    onPlatformItemClick: (BlogPlatform) -> Unit,
//) {
//    val bottomSheetNavigator = LocalBottomSheetNavigator.current
//    val snackbarHostState = rememberSnackbarHostState()
//    var topBarItems: List<BlogPlatform> by rememberSaveable {
//        mutableStateOf(emptyList())
//    }
//    Scaffold(
//        modifier = Modifier.navigationBarsPadding(),
//        snackbarHost = {
//            SnackbarHost(hostState = snackbarHostState)
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = onPostStatusClick,
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Add",
//                )
//            }
//        },
//        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
//    ) { paddings ->
//        val selectedIndex = uiState.tabIndex
//        LoadableLayout(
//            modifier = Modifier
//                .padding(paddings)
//                .fillMaxSize(),
//            state = uiState.pageUiStateList,
//        ) { feedsList ->
//            TopOutTopBarLayout(
//                modifier = Modifier.fillMaxSize(),
//                topBar = {
//                    if (topBarItems.isNotEmpty()) {
//                        FeedsHomeTopBar(
//                            modifier = Modifier,
//                            topBarItems = topBarItems,
//                            tabs = feedsList,
//                            selectedIndex = selectedIndex,
//                            onTabClick = onTabSelected,
//                            onServerItemClick = onPlatformItemClick,
//                            onMenuClick = {
//                                bottomSheetNavigator.show(
//                                    AllFeedsManagerScreen(
//                                        feedsList = uiState.pageUiStateList
//                                            .successDataOrNull() ?: emptyList(),
//                                        onItemClick = { index ->
//                                            onTabSelected(index)
//                                        }
//                                    ))
//                            },
//                        )
//                    }
//                },
//            ) {
//                if (feedsList.isNotEmpty()) {
//                    val pagerState = rememberPagerState(
//                        initialPage = selectedIndex,
//                        pageCount = feedsList::size,
//                    )
//                    LaunchedEffect(selectedIndex) {
//                        pagerState.scrollToPage(selectedIndex)
//                    }
//                    val currentPage = pagerState.currentPage
//                    LaunchedEffect(currentPage) {
//                        onTabSelected(currentPage)
//                    }
//                    HorizontalPager(
//                        modifier = Modifier.fillMaxSize(),
//                        state = pagerState,
//                        userScrollEnabled = true,
//                    ) { pageIndex ->
//                        val pagedUiState = feedsList[pageIndex]
//                        topBarItems = pagedUiState.platformList
//                        FeedsPage(
//                            uiState = pagedUiState,
//                            onRefresh = onRefresh,
//                            onLoadMore = onLoadMore,
//                            onShowSnackMessage = {
//                                snackbarHostState.showSnackbar(it)
//                            },
//                        )
//                    }
//                } else {
//                    Box(modifier = Modifier.fillMaxSize()) {
//                        Button(
//                            modifier = Modifier.align(Alignment.Center),
//                            onClick = onAddFeedsClick,
//                        ) {
//                            Text(text = "Add Feeds")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun FeedsHomeTopBar(
//    modifier: Modifier,
//    selectedIndex: Int,
//    tabs: List<FeedsScreenUiState>,
//    onTabClick: (Int) -> Unit,
//    topBarItems: List<BlogPlatform>,
//    onServerItemClick: (BlogPlatform) -> Unit,
//    onMenuClick: () -> Unit,
//) {
//    var showSelectSourcePopup by remember {
//        mutableStateOf(false)
//    }
//    Column(modifier = modifier) {
//        Column {
//            Row(
//                modifier = Modifier
//                    .padding(
//                        start = TopAppBarDefault.StartPadding,
//                        end = TopAppBarDefault.EndPadding
//                    )
//                    .height(TopAppBarDefault.TopBarHeight),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                TextWithIcon(
//                    modifier = Modifier.clickable {
//                        if (topBarItems.size > 1) {
//                            showSelectSourcePopup = true
//                        } else {
//                            onServerItemClick(topBarItems.first())
//                        }
//                    },
//                    text = topBarItems.first().name,
//                    fontSize = 18.sp,
//                    endIcon = {
//                        if (topBarItems.size > 1) {
//                            Icon(
//                                modifier = Modifier.padding(start = 4.dp),
//                                imageVector = Icons.Default.ArrowDropDown,
//                                contentDescription = "",
//                            )
//                        }
//                    }
//                )
//                Box(modifier = Modifier.weight(1F))
//                IconButton(onClick = onMenuClick) {
//                    Icon(
//                        painter = rememberVectorPainter(Icons.Default.Menu),
//                        contentDescription = "Menu",
//                    )
//                }
//            }
//
//            DropdownMenu(
//                offset = DpOffset(x = 30.dp, y = 8.dp),
//                expanded = showSelectSourcePopup,
//                onDismissRequest = { showSelectSourcePopup = false },
//            ) {
//                topBarItems.forEach { source ->
//                    DropdownMenuItem(
//                        onClick = { onServerItemClick(source) },
//                    ) {
//                        Text(text = source.name)
//                    }
//                }
//            }
//        }
//
//        UtopiaTabRow(
//            modifier = Modifier.fillMaxWidth(),
//            selectedTabIndex = selectedIndex,
//            tabCount = tabs.size,
//            containerColor = Color.Transparent,
//            selectedIndex = selectedIndex,
//            onTabClick = onTabClick,
//            tabContent = { index ->
//                Box(
//                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
//                ) {
//                    Text(
//                        text = tabs[index].name,
//                    )
//                }
//            }
//        )
//    }
//}
