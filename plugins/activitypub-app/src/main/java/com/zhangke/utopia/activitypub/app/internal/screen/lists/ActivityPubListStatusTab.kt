package com.zhangke.utopia.activitypub.app.internal.screen.lists

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.network.FormalBaseUrl

class ActivityPubListStatusTab(
    private val baseUrl: FormalBaseUrl,
    private val listId: String,
    private val listTitle: String,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = listTitle
        )

    @Composable
    override fun Screen.TabContent() {
        val viewModel = getViewModel<ActivityPubListStatusViewModel>().getSubViewModel(baseUrl, listId)
        TODO("Not yet implemented")
    }
}
