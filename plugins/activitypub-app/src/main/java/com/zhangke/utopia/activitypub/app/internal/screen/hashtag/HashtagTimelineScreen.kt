package com.zhangke.utopia.activitypub.app.internal.screen.hashtag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.utopia.status.model.Hashtag

@Destination(HashtagTimelineRoute.ROUTE)
class HashtagTimelineScreen(
    @Router private val route: String,
): Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val viewModel = getViewModel<HashtagTimelineViewModel, HashtagTimelineViewModel.Factory>(){
            it.create(HashtagTimelineRoute.parseRoute(route))
        }

    }
}
