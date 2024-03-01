package com.zhangke.utopia.activitypub.app.internal.screen.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
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
    override fun Screen.TabContent() {
        val viewModel = getViewModel<ActivityPubContentViewModel>().getSubViewModel(configId)
        val loadableState by viewModel.uiState.collectAsState()
        LoadableLayout(
            modifier = Modifier.fillMaxSize(),
            state = loadableState,
        ) { uiState ->
            ActivityPubContentUi(
                uiState = uiState,
            )
        }
    }

    @Composable
    private fun Screen.ActivityPubContentUi(
        uiState: ActivityPubContentUiState,
    ) {
        val tabList = remember(uiState) {
            createTabs(uiState)
        }
        HorizontalPagerWithTab(
            tabList = tabList,
        )
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
