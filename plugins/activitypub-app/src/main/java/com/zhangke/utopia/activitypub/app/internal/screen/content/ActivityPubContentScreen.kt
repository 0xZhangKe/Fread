package com.zhangke.utopia.activitypub.app.internal.screen.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.UtopiaTabRow
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubTimelineType
import com.zhangke.utopia.activitypub.app.internal.screen.lists.ActivityPubListStatusTab
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
            val tabList = remember(uiState, lists) {
                createScreens(uiState, lists)
            }
            val pagerState = rememberPagerState {
                tabList.size
            }
            UtopiaTabRow(
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
            type = ActivityPubTimelineType.HOME,
        )
        screenList += lists.map {
            ActivityPubListStatusTab(
                baseUrl = uiState.config.baseUrl,
                listId = it.id,
                listTitle = it.title,
            )
        }
        screenList += ActivityPubTimelineTab(
            baseUrl = uiState.config.baseUrl,
            type = ActivityPubTimelineType.LOCAL,
        )
        screenList += ActivityPubTimelineTab(
            baseUrl = uiState.config.baseUrl,
            type = ActivityPubTimelineType.PUBLIC,
        )
        screenList += TrendingStatusTab(
            baseUrl = uiState.config.baseUrl,
        )
        return screenList
    }
}
