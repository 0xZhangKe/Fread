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
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.screen.content.timeline.ActivityPubTimelineTab
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.instance.PlatformDetailRoute
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.fread.activitypub.app.internal.screen.trending.TrendingStatusTab
import com.zhangke.fread.analytics.HomeTabElements
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.common.ContentToolbar
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import kotlinx.coroutines.launch

class ActivityPubContentScreen(
    private val configId: Long,
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
            onTitleClick = {
                navigator.push(InstanceDetailScreen(PlatformDetailRoute.buildRoute(it.baseUrl)))
            },
            onPostBlogClick = {
                navigator.push(PostStatusScreen(PostStatusScreenRoute.buildRoute(it.uri)))
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ActivityPubContentUi(
        screen: Screen,
        uiState: ActivityPubContentUiState,
        onTitleClick: (ContentConfig.ActivityPubContent) -> Unit,
        onPostBlogClick: (ActivityPubLoggedAccount) -> Unit,
    ) {
        val (role, config, account, errorMessage) = uiState
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
                                account?.let(onPostBlogClick)
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
                    if (role != null && config != null) {
                        val tabList = remember(uiState) {
                            createTabs(role, config)
                        }
                        val pagerState = rememberPagerState(0) {
                            tabList.size
                        }
                        TopBarWithTabLayout(
                            topBarContent = {
                                ContentToolbar(
                                    title = config.configName,
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
                                    onTitleClick = {
                                        reportClick(HomeTabElements.TITLE)
                                        onTitleClick(config)
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
                    } else if (errorMessage.isNullOrBlank().not()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 64.dp, end = 16.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter),
                                text = errorMessage.orEmpty(),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createTabs(
        role: IdentityRole,
        config: ContentConfig.ActivityPubContent,
    ): List<PagerTab> {
        return config
            .showingTabList
            .sortedBy { it.order }
            .map { it.toPagerTab(role) }
    }

    private fun ContentConfig.ActivityPubContent.ContentTab.toPagerTab(
        role: IdentityRole,
    ): PagerTab {
        return when (this) {
            is ContentConfig.ActivityPubContent.ContentTab.HomeTimeline -> {
                ActivityPubTimelineTab(
                    role = role,
                    type = ActivityPubStatusSourceType.TIMELINE_HOME,
                )
            }

            is ContentConfig.ActivityPubContent.ContentTab.LocalTimeline -> {
                ActivityPubTimelineTab(
                    role = role,
                    type = ActivityPubStatusSourceType.TIMELINE_LOCAL,
                )
            }

            is ContentConfig.ActivityPubContent.ContentTab.PublicTimeline -> {
                ActivityPubTimelineTab(
                    role = role,
                    type = ActivityPubStatusSourceType.TIMELINE_PUBLIC,
                )
            }

            is ContentConfig.ActivityPubContent.ContentTab.Trending -> {
                TrendingStatusTab(
                    role = role,
                )
            }

            is ContentConfig.ActivityPubContent.ContentTab.ListTimeline -> {
                ActivityPubTimelineTab(
                    role = role,
                    type = ActivityPubStatusSourceType.LIST,
                    listId = listId,
                    listTitle = name,
                )
            }
        }
    }
}
