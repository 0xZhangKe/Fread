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
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentScreen
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsTab
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostScreen
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.commonbiz.shared.composable.NotLoginPageError
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.common.ContentToolbar
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
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
        nestedScrollConnection: NestedScrollConnection?,
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
            onPostBlogClick = { uiState.locator?.let { navigator.push(PublishPostScreen(it)) } },
            onTitleClick = {},
            onLoginClick = {
                uiState.content?.baseUrl?.let { baseUrl ->
                    navigator.push(
                        AddBlueskyContentScreen(
                            baseUrl = baseUrl,
                            loginMode = true
                        )
                    )
                }
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
        onLoginClick: () -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val mainTabConnection = LocalNestedTabConnection.current
        val showFb = uiState.account != null
        Scaffold(
            floatingActionButton = {
                if (showFb) {
                    val inImmersiveMode by mainTabConnection.inImmersiveFlow.collectAsState()
                    val immersiveNavBar = LocalStatusUiConfig.current.immersiveNavBar
                    AnimatedVisibility(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = 100.dp),
                        visible = !inImmersiveMode || !immersiveNavBar,
                        enter = scaleIn() + slideInVertically(
                            initialOffsetY = { it },
                        ),
                        exit = scaleOut() + slideOutVertically(
                            targetOffsetY = { it },
                        ),
                    ) {
                        FloatingActionButton(
                            onClick = { onPostBlogClick(uiState.account) },
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
                            remember(uiState) { createTabList(uiState.content, uiState.locator) }
                        val pagerState = rememberPagerState(0) { tabList.size }
                        TopBarWithTabLayout(
                            topBarContent = {
                                ContentToolbar(
                                    title = uiState.content.name,
                                    showNextIcon = !isLatestContent,
                                    onMenuClick = {
                                        coroutineScope.launch {
                                            mainTabConnection.openDrawer()
                                        }
                                    },
                                    onNextClick = {
                                        coroutineScope.launch {
                                            mainTabConnection.switchToNextTab()
                                        }
                                    },
                                    onRefreshClick = {
                                        coroutineScope.launch {
                                            mainTabConnection.scrollToTop()
                                            mainTabConnection.refresh()
                                        }
                                    },
                                    onTitleClick = {
                                        onTitleClick(uiState.content)
                                    },
                                    onDoubleClick = {
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
                            if (!uiState.initializing && tabList.isEmpty() && uiState.account == null) {
                                NotLoginPageError(
                                    modifier = Modifier.padding(top = 64.dp),
                                    message = null,
                                    onLoginClick = onLoginClick,
                                )
                            } else {
                                val contentScrollInProgress by mainTabConnection.contentScrollInpProgress.collectAsState()
                                HorizontalPager(
                                    modifier = Modifier.fillMaxSize(),
                                    state = pagerState,
                                    userScrollEnabled = !contentScrollInProgress,
                                ) { pageIndex ->
                                    tabList[pageIndex].TabContent(
                                        screen,
                                        null,
                                    )
                                }
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
                    } else if (!uiState.initializing && uiState.account == null) {
                        // not login
                        NotLoginPageError(
                            modifier = Modifier.padding(top = 64.dp),
                            message = null,
                            onLoginClick = onLoginClick,
                        )
                    }
                }
            }
        }
    }

    private fun createTabList(
        content: BlueskyContent,
        locator: PlatformLocator?,
    ): List<HomeFeedsTab> {
        if (locator == null) return emptyList()
        return content.feedsList.filter { it.pinned }
            .map { HomeFeedsTab(feeds = it, locator = locator) }
    }
}
