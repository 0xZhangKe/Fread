package com.zhangke.fread.activitypub.app.internal.screen.content

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
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.TopBarWithTabLayout
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.screen.content.timeline.ActivityPubTimelineTab
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreen
import com.zhangke.fread.activitypub.app.internal.screen.trending.TrendingStatusTab
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.common.ContentToolbar
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import kotlinx.coroutines.launch

internal class ActivityPubContentScreen(
    private val configId: String,
    private val isLatestContent: Boolean,
) : BasePagerTab() {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        super.TabContent(screen, nestedScrollConnection)
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = screen.getViewModel<ActivityPubContentViewModel>().getSubViewModel(configId)
        val uiState by viewModel.uiState.collectAsState()
        ActivityPubContentUi(
            screen = screen,
            uiState = uiState,
            onTitleClick = { content ->
                uiState.locator?.let {
                    navigator.push(InstanceDetailScreen(it, content.baseUrl))
                }
            },
            onPostBlogClick = {
                navigator.push(PostStatusScreen(accountUri = it.uri))
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ActivityPubContentUi(
        screen: Screen,
        uiState: ActivityPubContentUiState,
        onTitleClick: (ActivityPubContent) -> Unit,
        onPostBlogClick: (ActivityPubLoggedAccount) -> Unit,
    ) {
        val (locator, config, account, errorMessage) = uiState
        val coroutineScope = rememberCoroutineScope()
        val mainTabConnection = LocalNestedTabConnection.current
        val snackBarHostState = rememberSnackbarHostState()
        val showFb = account != null
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = if (showFb) 0.dp else 68.dp),
                    hostState = snackBarHostState,
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                            onClick = {
                                onPostBlogClick(account)
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
        ) { paddings ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
            ) {
                CompositionLocalProvider(
                    LocalSnackbarHostState provides snackBarHostState,
                ) {
                    if (locator != null && config != null) {
                        val tabList = remember(uiState) {
                            createTabs(locator, config)
                        }
                        val pagerState = rememberPagerState(0) {
                            tabList.size
                        }
                        TopBarWithTabLayout(
                            topBarContent = {
                                ContentToolbar(
                                    title = config.name,
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
                                        onTitleClick(config)
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
                                            text = tabList[it].options?.title.orEmpty(),
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
                    } else if (!errorMessage.isNullOrBlank()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 64.dp, end = 16.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter),
                                text = errorMessage,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createTabs(
        locator: PlatformLocator,
        config: ActivityPubContent,
    ): List<PagerTab> {
        return config
            .tabList
            .filter { !it.hide }
            .sortedBy { it.order }
            .map { it.toPagerTab(locator) }
    }

    private fun ActivityPubContent.ContentTab.toPagerTab(
        locator: PlatformLocator,
    ): PagerTab {
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
