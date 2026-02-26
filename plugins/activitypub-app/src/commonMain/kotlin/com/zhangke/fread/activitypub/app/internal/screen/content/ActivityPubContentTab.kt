package com.zhangke.fread.activitypub.app.internal.screen.content

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.screen.content.timeline.ActivityPubTimelineTab
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.trending.TrendingStatusTab
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.common.HomeContentTabsTopBar
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.PublishingFab
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

internal class ActivityPubContentTab(
    private val configId: String,
    private val isLatestContent: Boolean,
) : BaseTab() {

    override val options: TabOptions?
        @Composable get() = null

    @Composable
    override fun Content() {
        super.Content()
        val navBackStack = LocalNavBackStack.currentOrThrow
        val viewModel = koinViewModel<ActivityPubContentViewModel>().getSubViewModel(configId)
        val uiState by viewModel.uiState.collectAsState()
        ActivityPubContentUi(
            uiState = uiState,
            onTitleClick = { content ->
                uiState.locator?.let {
                    navBackStack.add(InstanceDetailScreenKey(it, content.baseUrl))
                }
            },
            onPostBlogClick = {
                navBackStack.add(PostStatusScreenKey(accountUri = it.uri))
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ActivityPubContentUi(
        uiState: ActivityPubContentUiState,
        onTitleClick: (ActivityPubContent) -> Unit,
        onPostBlogClick: (ActivityPubLoggedAccount) -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val mainTabConnection = LocalNestedTabConnection.current
        val snackBarHostState = rememberSnackbarHostState()
        val showFb = uiState.account != null
        val tabList = remember(uiState.locator, uiState.config) {
            if (uiState.locator != null && uiState.config != null) {
                createTabs(uiState.locator, uiState.config)
            } else {
                emptyList()
            }
        }
        val tabTitles = tabList.map { it.options?.title.orEmpty() }
        val pagerState = rememberPagerState(0) { tabList.size }
        val topBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topBarState)
        LaunchedEffect(mainTabConnection, topBarState) {
            mainTabConnection.scrollToTopFlow.collect {
                topBarState.contentOffset = 0F
                topBarState.heightOffset = 0F
            }
        }
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if (uiState.config != null && tabList.isNotEmpty()) {
                    HomeContentTabsTopBar(
                        title = uiState.config.name,
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
                            onTitleClick(uiState.config)
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
            snackbarHost = {
                SnackbarHost(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .contentBottomPadding(),
                    hostState = snackBarHostState,
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
        ) { paddings ->
            CompositionLocalProvider(
                LocalSnackbarHostState provides snackBarHostState,
                LocalContentPadding provides plusContentPadding(paddings),
            ) {
                if (uiState.locator != null && uiState.config != null) {
                    if (tabList.isNotEmpty()) {
                        val contentScrollInProgress by mainTabConnection.contentScrollInpProgress.collectAsState()
                        HorizontalPager(
                            modifier = Modifier.fillMaxSize(),
                            state = pagerState,
                            userScrollEnabled = !contentScrollInProgress,
                        ) { pageIndex ->
                            tabList[pageIndex].Content()
                        }
                    }
                } else if (!uiState.errorMessage.isNullOrBlank()) {
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
                }
            }
        }
    }

    private fun createTabs(
        locator: PlatformLocator,
        config: ActivityPubContent,
    ): List<Tab> {
        return config
            .tabList
            .filter { !it.hide }
            .sortedBy { it.order }
            .map { it.toPagerTab(locator) }
    }

    private fun ActivityPubContent.ContentTab.toPagerTab(locator: PlatformLocator): Tab {
        return when (this) {
            is ActivityPubContent.ContentTab.HomeTimeline -> {
                ActivityPubTimelineTab(
                    locator = locator,
                    type = ActivityPubStatusSourceType.TIMELINE_HOME,
                )
            }

            is ActivityPubContent.ContentTab.LocalTimeline -> {
                ActivityPubTimelineTab(
                    locator = locator,
                    type = ActivityPubStatusSourceType.TIMELINE_LOCAL,
                )
            }

            is ActivityPubContent.ContentTab.PublicTimeline -> {
                ActivityPubTimelineTab(
                    locator = locator,
                    type = ActivityPubStatusSourceType.TIMELINE_PUBLIC,
                )
            }

            is ActivityPubContent.ContentTab.Trending -> {
                TrendingStatusTab(locator = locator)
            }

            is ActivityPubContent.ContentTab.ListTimeline -> {
                ActivityPubTimelineTab(
                    locator = locator,
                    type = ActivityPubStatusSourceType.LIST,
                    listId = listId,
                    listTitle = name,
                )
            }
        }
    }
}
