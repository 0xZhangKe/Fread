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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.TopBarWithTabLayout
import com.zhangke.framework.composable.FreadTabRow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.screen.content.timeline.ActivityPubTimelineTab
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.instance.PlatformDetailRoute
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.fread.activitypub.app.internal.screen.trending.TrendingStatusTab
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.common.ContentToolbar
import com.zhangke.fread.status.ui.common.LocalMainTabConnection
import kotlinx.coroutines.launch

class ActivityPubContentScreen(
    private val configId: Long,
    private val isLatestContent: Boolean,
) : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<ActivityPubContentViewModel>().getSubViewModel(configId)
        val uiState by viewModel.uiState.collectAsState()
        ActivityPubContentUi(
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
    private fun Screen.ActivityPubContentUi(
        uiState: ActivityPubContentUiState,
        onTitleClick: (ContentConfig.ActivityPubContent) -> Unit,
        onPostBlogClick: (ActivityPubLoggedAccount) -> Unit,
    ) {
        val (role, config, account, errorMessage) = uiState
        val coroutineScope = rememberCoroutineScope()
        val mainTabConnection = LocalMainTabConnection.current
        val snackbarHostState = rememberSnackbarHostState()
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                if (account != null) {
                    val inImmersiveMode by mainTabConnection.inImmersiveFlow.collectAsState()
                    AnimatedVisibility(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = 60.dp),
                        visible = !inImmersiveMode,
                        enter = scaleIn() + slideInVertically(
                            initialOffsetY = { it },
                        ),
                        exit = scaleOut() + slideOutVertically(
                            targetOffsetY = { it },
                        ),
                    ) {
                        FloatingActionButton(
                            onClick = { onPostBlogClick(account) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Post Micro Blog",
                            )
                        }
                    }
                }
            }
        ) { paddings ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
            ) {
                CompositionLocalProvider(
                    LocalSnackbarHostState provides snackbarHostState,
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
                                        coroutineScope.launch {
                                            mainTabConnection.openDrawer()
                                        }
                                    },
                                    onNextClick = {
                                        coroutineScope.launch {
                                            mainTabConnection.switchToNextTab()
                                        }
                                    },
                                    onTitleClick = {
                                        onTitleClick(config)
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
                            HorizontalPager(
                                modifier = Modifier.fillMaxSize(),
                                state = pagerState,
                            ) { pageIndex ->
                                with(tabList[pageIndex]) {
                                    TabContent(null)
                                }
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
