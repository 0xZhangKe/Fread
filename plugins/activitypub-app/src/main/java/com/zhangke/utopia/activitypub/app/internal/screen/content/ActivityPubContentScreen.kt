package com.zhangke.utopia.activitypub.app.internal.screen.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubTimelineType
import com.zhangke.utopia.activitypub.app.internal.screen.lists.ActivityPubListStatusTab
import com.zhangke.utopia.activitypub.app.internal.screen.timeline.ActivityPubTimelineTab
import com.zhangke.utopia.activitypub.app.internal.screen.trending.TrendingStatusTab
import com.zhangke.utopia.status.model.ContentConfig

class ActivityPubContentScreen(
    private val configId: Long,
) : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val viewModel = getViewModel<ActivityPubContentViewModel>().getSubViewModel(configId)
        val uiState by viewModel.uiState.collectAsState()
        ActivityPubContentUi(
            uiState = uiState,
            nestedScrollConnection = nestedScrollConnection,
        )
    }

    @Composable
    private fun Screen.ActivityPubContentUi(
        uiState: LoadableState<ActivityPubContentUiState>,
        nestedScrollConnection: NestedScrollConnection?,
    ) {
        when (uiState) {
            is LoadableState.Failed -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = uiState.exception.message.orEmpty(),
                    )
                }
            }

            is LoadableState.Idle, is LoadableState.Loading -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .utopiaPlaceholder(true),
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .weight(1F)
                            .utopiaPlaceholder(true),
                    )
                }
            }

            is LoadableState.Success -> {
                val tabList = remember(uiState) {
                    createTabs(uiState.data)
                }
                HorizontalPagerWithTab(
                    tabList = tabList,
                    nestedScrollConnection = nestedScrollConnection,
                )
            }
        }
    }

    private fun createTabs(
        uiState: ActivityPubContentUiState,
    ): List<PagerTab> {
        return uiState.config
            .showingTabList
            .sortedBy { it.order }
            .map { it.toPagerTab(uiState.config.baseUrl) }
    }

    private fun ContentConfig.ActivityPubContent.ContentTab.toPagerTab(
        baseUrl: FormalBaseUrl,
    ): PagerTab {
        return when (this) {
            is ContentConfig.ActivityPubContent.ContentTab.HomeTimeline -> {
                ActivityPubTimelineTab(
                    baseUrl = baseUrl,
                    type = ActivityPubTimelineType.HOME,
                )
            }

            is ContentConfig.ActivityPubContent.ContentTab.LocalTimeline -> {
                ActivityPubTimelineTab(
                    baseUrl = baseUrl,
                    type = ActivityPubTimelineType.LOCAL,
                )
            }

            is ContentConfig.ActivityPubContent.ContentTab.PublicTimeline -> {
                ActivityPubTimelineTab(
                    baseUrl = baseUrl,
                    type = ActivityPubTimelineType.PUBLIC,
                )
            }

            is ContentConfig.ActivityPubContent.ContentTab.Trending -> {
                TrendingStatusTab(
                    baseUrl = baseUrl,
                )
            }

            is ContentConfig.ActivityPubContent.ContentTab.ListTimeline -> {
                ActivityPubListStatusTab(
                    baseUrl = baseUrl,
                    listId = listId,
                    listTitle = name,
                )
            }
        }
    }
}
