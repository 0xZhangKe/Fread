package com.zhangke.utopia.activitypub.app.internal.screen.hashtag

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionScene
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.collapsable.CollapsableTopBarLayout
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.utopia.activitypub.app.internal.screen.content.ActivityPubListStatusContent
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.flow.SharedFlow

@Destination(HashtagTimelineRoute.ROUTE)
class HashtagTimelineScreen(
    @Router private val route: String = "",
) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val viewModel = getViewModel<HashtagTimelineViewModel, HashtagTimelineViewModel.Factory> {
            it.create(HashtagTimelineRoute.parseRoute(route))
        }

        val uiState by viewModel.statusUiState.collectAsState()
    }

    @Composable
    private fun HashtagTimelineContent(
        hashtagTimelineUiState: HashtagTimelineUiState,
        statusUiState: CommonLoadableUiState<StatusUiState>,
        messageFlow: SharedFlow<TextString>,
        onBackClick: () -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        CollapsableTopBarLayout(minTopBarHeight =, contentCanScrollBackward =, topBar =) {

        }
        Scaffold(
            topBar = {
                Toolbar(
                    onBackClick = onBackClick,
                    title = hashtagTimelineUiState.hashTag,
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                ActivityPubListStatusContent(
                    uiState = statusUiState,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                    onInteractive = onInteractive,
                )
            }
        }
        ConsumeSnackbarFlow(snackbarHostState, messageFlow)
    }

    @OptIn(ExperimentalMotionApi::class)
    @Composable
    private fun buildMotionScene() = MotionScene {
        val topBarRef = createRefFor("topBar")
        val topBarBottomContentRef = createRefFor("topBarBottomContent")
        val start1 = constraintSet {
            constrain(topBarRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
            constrain(topBarBottomContentRef) {
                top.linkTo(topBarRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        }
        val end1 = constraintSet {
            constrain(topBarRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
            constrain(topBarBottomContentRef) {
                bottom.linkTo(topBarRef.bottom, 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        }
        transition("default", start1, end1) {}
    }
}
