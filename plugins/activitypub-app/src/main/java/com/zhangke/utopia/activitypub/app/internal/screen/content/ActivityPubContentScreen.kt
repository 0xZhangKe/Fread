package com.zhangke.utopia.activitypub.app.internal.screen.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.screen.timeline.ActivityPubTimelineScreen
import com.zhangke.utopia.activitypub.app.internal.screen.trending.TrendingStatusScreen

class ActivityPubContentScreen(
    private val configId: Long,
) : AndroidScreen() {

    @OptIn(ExperimentalFoundationApi::class)
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
            val screenList: List<Screen> = remember {
                listOf(
                    ActivityPubTimelineScreen(uiState.config.baseUrl, TimelineSourceType.HOME),
                    ActivityPubTimelineScreen(uiState.config.baseUrl, TimelineSourceType.LOCAL),
                    ActivityPubTimelineScreen(uiState.config.baseUrl, TimelineSourceType.PUBLIC),
                    TrendingStatusScreen(uiState.config.baseUrl),
                )
            }
            val pagerState = rememberPagerState {
                screenList.size
            }
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { pageIndex ->
                Navigator(screenList[pageIndex])
            }
        }
    }
}
