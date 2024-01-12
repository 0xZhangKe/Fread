package com.zhangke.utopia.activitypub.app.internal.screen.content

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.screen.timeline.ActivityPubTimelineScreen
import com.zhangke.utopia.activitypub.app.internal.screen.trending.TrendingStatusScreen
import kotlinx.coroutines.launch

class ActivityPubContentScreen(
    private val configId: Long,
) : AndroidScreen() {

    @Composable
    override fun Content() {
        val viewModel: ActivityPubContentViewModel = getViewModel()
        val loadableState by viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.configId = configId
            viewModel.onPrepared()
        }
        LoadableLayout(
            modifier = Modifier.fillMaxSize(),
            state = loadableState,
        ) { uiState ->
            ActivityPubContentUi(uiState)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ActivityPubContentUi(
        uiState: ActivityPubContentUiState,
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        Column(modifier = Modifier.fillMaxSize()) {
            val tabList = remember {
                createScreens(context, uiState)
            }
            val pagerState = rememberPagerState {
                tabList.size
            }
            TabRow(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage,
            ) {
                tabList.forEachIndexed { index, item ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                    ) {
                        Box(
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        ) {
                            Text(text = item.title)
                        }
                    }
                }
            }
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { pageIndex ->
                Navigator(
                    tabList[pageIndex].screen,
                    disposeBehavior = NavigatorDisposeBehavior(disposeNestedNavigators = false),
                )
            }
        }
    }

    private fun createScreens(
        context: Context,
        uiState: ActivityPubContentUiState,
    ): List<ActivityPubContentTab> {
        val screenList = mutableListOf<ActivityPubContentTab>()
        screenList += ActivityPubContentTab(
            title = context.getString(R.string.activity_pub_content_tab_home),
            screen = ActivityPubTimelineScreen(uiState.config.baseUrl, TimelineSourceType.HOME),
        )
        screenList += ActivityPubContentTab(
            title = context.getString(R.string.activity_pub_content_tab_trending),
            screen = TrendingStatusScreen(uiState.config.baseUrl),
        )
        screenList += ActivityPubContentTab(
            title = context.getString(R.string.activity_pub_content_tab_local_timeline),
            screen = ActivityPubTimelineScreen(uiState.config.baseUrl, TimelineSourceType.LOCAL),
        )
        screenList += ActivityPubContentTab(
            title = context.getString(R.string.activity_pub_content_tab_public_timeline),
            screen = ActivityPubTimelineScreen(uiState.config.baseUrl, TimelineSourceType.PUBLIC),
        )
        return screenList
    }

    class ActivityPubContentTab(
        val title: String,
        val screen: Screen,
    )
}
