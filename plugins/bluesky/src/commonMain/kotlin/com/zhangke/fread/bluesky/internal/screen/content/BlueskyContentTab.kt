package com.zhangke.fread.bluesky.internal.screen.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.contentBottomPadding
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.plusContentPadding
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsTab
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostScreenNavKey
import com.zhangke.fread.commonbiz.shared.composable.NotLoginPageError
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.common.HomeContentTabsTopBar
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.PublishingFab
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

class BlueskyContentTab(
    private val contentId: String,
    private val isLatestContent: Boolean,
) : BaseTab() {

    override val options: TabOptions?
        @Composable get() = null

    @Composable
    override fun Content() {
        super.Content()
        val backStack = LocalNavBackStack.currentOrThrow
        val viewModel = koinViewModel<BlueskyContentContainerViewModel>().getSubViewModel(contentId)
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = rememberSnackbarHostState()
        BlueskyHomeContent(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onPostBlogClick = {
                uiState.locator?.let { backStack.add(PublishPostScreenNavKey(locator = it)) }
            },
            onTitleClick = {},
            onLoginClick = {
                uiState.content?.baseUrl?.let { baseUrl ->
                    backStack.add(
                        AddBlueskyContentScreenNavKey(
                            baseUrl = baseUrl,
                            loginMode = true,
                        )
                    )
                }
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BlueskyHomeContent(
        uiState: BlueskyContentUiState,
        snackBarHostState: SnackbarHostState,
        onPostBlogClick: (BlueskyLoggedAccount) -> Unit,
        onTitleClick: (BlueskyContent) -> Unit,
        onLoginClick: () -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val mainTabConnection = LocalNestedTabConnection.current
        val showFb = uiState.account != null
        val tabList = remember(uiState.locator, uiState.content) {
            if (uiState.content != null) {
                createTabList(uiState.content, uiState.locator)
            } else {
                emptyList()
            }
        }
        val tabTitles = tabList.map { it.options.title }
        val pagerState = rememberPagerState(0) { tabList.size }
        val topBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topBarState)
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if (uiState.content != null && tabList.isNotEmpty()) {
                    HomeContentTabsTopBar(
                        title = uiState.content.name,
                        account = uiState.account,
                        showAccountInfo = uiState.showAccountInTopBar,
                        selectedTabIndex = pagerState.currentPage,
                        tabTitles = tabTitles,
                        scrollBehavior = scrollBehavior,
                        showNextIcon = !isLatestContent && uiState.showNextButton,
                        showRefreshButton = uiState.showRefreshButton,
                        onMenuClick = {
                            coroutineScope.launch {
                                mainTabConnection.openDrawer()
                            }
                        },
                        onRefreshClick = {
                            coroutineScope.launch {
                                mainTabConnection.scrollToTop()
                                mainTabConnection.refresh()
                            }
                        },
                        onNextClick = {
                            coroutineScope.launch {
                                mainTabConnection.switchToNextTab()
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
                        onTabClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(it)
                            }
                        },
                    )
                }
            },
            floatingActionButton = {
                if (showFb) {
                    val inImmersiveMode by mainTabConnection.inImmersiveFlow.collectAsState()
                    val immersiveNavBar = LocalStatusUiConfig.current.immersiveNavBar
                    PublishingFab(
                        visible = !inImmersiveMode || !immersiveNavBar,
                        onPublishClick = { onPostBlogClick(uiState.account) },
                    )
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .contentBottomPadding(),
                    hostState = snackBarHostState,
                )
            },
        ) { paddings ->
            CompositionLocalProvider(
                LocalSnackbarHostState provides snackBarHostState,
                LocalContentPadding provides plusContentPadding(paddings),
            ) {
                if (uiState.content != null) {
                    if (uiState.authFailed) {
                        NotLoginPageError(
                            modifier = Modifier
                                .padding(LocalContentPadding.current)
                                .padding(top = 64.dp),
                            message = null,
                            onLoginClick = onLoginClick,
                        )
                    } else if (tabList.isNotEmpty()) {
                        val contentScrollInProgress by mainTabConnection.contentScrollInpProgress.collectAsState()
                        HorizontalPager(
                            modifier = Modifier.fillMaxSize(),
                            state = pagerState,
                            userScrollEnabled = !contentScrollInProgress,
                        ) { pageIndex ->
                            tabList[pageIndex].Content()
                        }
                    }
                } else if (uiState.errorMessage != null) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .padding(LocalContentPadding.current),
                    ) {
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
                        modifier = Modifier
                            .padding(LocalContentPadding.current)
                            .padding(top = 64.dp),
                        message = null,
                        onLoginClick = onLoginClick,
                    )
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
