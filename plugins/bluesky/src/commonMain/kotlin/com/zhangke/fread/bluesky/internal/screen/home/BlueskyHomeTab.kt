package com.zhangke.fread.bluesky.internal.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.FreadTabRow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.TopBarWithTabLayout
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.analytics.HomeTabElements
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsTab
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.common.ContentToolbar
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import kotlinx.coroutines.launch

class BlueskyHomeTab(
    private val contentId: String,
    private val isLatestContent: Boolean,
) : BasePagerTab() {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?
    ) {
        super.TabContent(screen, nestedScrollConnection)
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            screen.getViewModel<BlueskyHomeContainerViewModel>().getSubViewModel(contentId)
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = rememberSnackbarHostState()
        BlueskyHomeContent(
            screen = screen,
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onPostBlogClick = {},
            onTitleClick = {

            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BlueskyHomeContent(
        screen: Screen,
        uiState: BlueskyHomeUiState,
        snackBarHostState: SnackbarHostState,
        onPostBlogClick: (BlueskyLoggedAccount) -> Unit,
        onTitleClick: (BlueskyContent) -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val mainTabConnection = LocalNestedTabConnection.current
        val showFb = uiState.account != null
        Scaffold(
            floatingActionButton = {
                if (showFb) {
                    val inImmersiveMode by mainTabConnection.inImmersiveFlow.collectAsState()
                    AnimatedVisibility(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = 100.dp),
                        visible = !inImmersiveMode,
                        enter = scaleIn() + slideInVertically(
                            initialOffsetY = { it },
                        ),
                        exit = scaleOut() + slideOutVertically(
                            targetOffsetY = { it },
                        ),
                    ) {
                        FloatingActionButton(
                            onClick = {
                                reportClick(HomeTabElements.POST_STATUS)
                                uiState.account?.let(onPostBlogClick)
                            },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Post Micro Blog",
                            )
                        }
                    }
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = if (showFb) 0.dp else 68.dp),
                    hostState = snackBarHostState,
                )
            }
        ) { paddings ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
            ) {
                CompositionLocalProvider(
                    LocalSnackbarHostState provides snackBarHostState,
                ) {
                    if (uiState.content != null) {
                        val tabList =
                            remember(uiState) { createTabList(uiState.content, uiState.role) }
                        val pagerState = rememberPagerState(0) {
                            tabList.size
                        }
                        TopBarWithTabLayout(
                            topBarContent = {
                                ContentToolbar(
                                    title = uiState.content.name,
                                    showNextIcon = !isLatestContent,
                                    onMenuClick = {
                                        reportClick(HomeTabElements.SHOW_DRAWER)
                                        coroutineScope.launch {
                                            mainTabConnection.openDrawer()
                                        }
                                    },
                                    onNextClick = {
                                        reportClick(HomeTabElements.NEXT)
                                        coroutineScope.launch {
                                            mainTabConnection.switchToNextTab()
                                        }
                                    },
                                    onRefreshClick = {
                                        reportClick(HomeTabElements.REFRESH)
                                        coroutineScope.launch {
                                            mainTabConnection.scrollToTop()
                                            mainTabConnection.refresh()
                                        }
                                    },
                                    onTitleClick = {
                                        reportClick(HomeTabElements.TITLE)
                                        onTitleClick(uiState.content)
                                    },
                                    onDoubleClick = {
                                        reportClick(HomeTabElements.TITLE_DOUBLE_CLICK)
                                        coroutineScope.launch {
                                            mainTabConnection.scrollToTop()
                                        }
                                    },
                                )
                            },
                            tabContent = {
                                FreadTabRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    selectedTabIndex = pagerState.currentPage,
                                    tabCount = tabList.size,
                                    tabContent = {
                                        Text(
                                            text = tabList[it].options.title,
                                            maxLines = 1,
                                        )
                                    },
                                    onTabClick = {
                                        coroutineScope.launch {
                                            pagerState.scrollToPage(it)
                                        }
                                    }
                                )
                            },
                        ) {
                            val contentScrollInProgress by mainTabConnection.contentScrollInpProgress.collectAsState()
                            HorizontalPager(
                                modifier = Modifier.fillMaxSize(),
                                state = pagerState,
                                userScrollEnabled = !contentScrollInProgress,
                            ) { pageIndex ->
                                tabList[pageIndex].TabContent(screen, null)
                            }
                        }
                    } else if (uiState.errorMessage != null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 64.dp, end = 16.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter),
                                text = uiState.errorMessage,
                                textAlign = TextAlign.Center,
                            )
                        }
                    } else if (uiState.account == null) {
                        // not login
                    }
                }
            }
        }
    }

    private fun createTabList(content: BlueskyContent, role: IdentityRole): List<HomeFeedsTab> {
        return content.feedsList.filter { it.pinned }
            .map { HomeFeedsTab(null, it, role) }
    }
}
