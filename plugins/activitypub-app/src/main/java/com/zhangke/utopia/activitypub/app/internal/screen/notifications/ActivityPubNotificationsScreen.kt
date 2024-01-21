package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights

class ActivityPubNotificationsScreen(
    private val userUriInsights: UserUriInsights,
) : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun Screen.TabContent() {
        val viewModel = getViewModel<ActivityPubNotificationsViewModel>()
            .getSubViewModel(userUriInsights)

    }

    @Composable
    private fun ActivityPubNotificationsContent() {
        MultiChoiceSegmentedButtonRow
    }
}
