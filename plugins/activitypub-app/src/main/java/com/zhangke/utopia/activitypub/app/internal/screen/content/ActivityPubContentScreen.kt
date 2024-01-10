package com.zhangke.utopia.activitypub.app.internal.screen.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType

class ActivityPubContentScreen(
    private val configId: Long,
) : AndroidScreen() {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val screenList: List<Screen> = remember {
            listOf(
                ActivityPubTimelineScreen(configId, TimelineSourceType.HOME),
                ActivityPubTimelineScreen(configId, TimelineSourceType.LOCAL),
                ActivityPubTimelineScreen(configId, TimelineSourceType.PUBLIC),
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
