package com.zhangke.utopia.activitypub.app.internal.screen.content

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.screen.timeline.ActivityPubTimelineTab
import com.zhangke.utopia.activitypub.app.internal.screen.trending.TrendingStatusTab
import kotlinx.coroutines.launch

class ActivityPubContentScreen(
    private val configId: Long,
) : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent() {
        val viewModel =
            getViewModel<ActivityPubContentViewModel, ActivityPubContentViewModel.Factory> {
                it.create(configId)
            }
        val loadableState by viewModel.uiState.collectAsState()
        val lists by viewModel.lists.collectAsState()
        LoadableLayout(
            modifier = Modifier.fillMaxSize(),
            state = loadableState,
        ) { uiState ->
            ActivityPubContentUi(
                uiState = uiState,
                lists = lists,
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun Screen.ActivityPubContentUi(
        uiState: ActivityPubContentUiState,
        lists: List<ActivityPubListEntity>,
    ) {
        val coroutineScope = rememberCoroutineScope()
        Column(modifier = Modifier.fillMaxSize()) {
            val tabList = remember {
                createScreens(uiState, lists)
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
                            Text(text = item.options?.title.orEmpty())
                        }
                    }
                }
            }
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { pageIndex ->
                with(tabList[pageIndex]) {
                    TabContent()
                }
            }
        }
    }

    private fun createScreens(
        uiState: ActivityPubContentUiState,
        lists: List<ActivityPubListEntity>,
    ): List<PagerTab> {
        val screenList = mutableListOf<PagerTab>()
        screenList += ActivityPubTimelineTab(
            baseUrl = uiState.config.baseUrl,
            type = TimelineSourceType.HOME,
        )
        screenList += TrendingStatusTab(
            baseUrl = uiState.config.baseUrl,
        )
        screenList += ActivityPubTimelineTab(
            baseUrl = uiState.config.baseUrl,
            type = TimelineSourceType.LOCAL,
        )
        screenList += ActivityPubTimelineTab(
            baseUrl = uiState.config.baseUrl,
            type = TimelineSourceType.PUBLIC,
        )
        return screenList
    }
}
