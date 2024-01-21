package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ActivityPubNotificationsContent() {
        Column(modifier = Modifier.fillMaxSize()) {
            MultiChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ){

            }
        }
    }
}
