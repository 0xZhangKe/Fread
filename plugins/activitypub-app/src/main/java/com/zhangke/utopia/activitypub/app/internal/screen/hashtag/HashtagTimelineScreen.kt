package com.zhangke.utopia.activitypub.app.internal.screen.hashtag

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.StyledTextButton
import com.zhangke.framework.composable.TextButtonStyle
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.collapsable.CollapsableTopBarLayout
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.utopia.activitypub.app.R
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
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<HashtagTimelineViewModel, HashtagTimelineViewModel.Factory> {
            it.create(HashtagTimelineRoute.parseRoute(route))
        }

        val hashtagTimelineUiState by viewModel.hashtagTimelineUiState.collectAsState()
        val statusUiState by viewModel.statusUiState.collectAsState()
        HashtagTimelineContent(
            hashtagTimelineUiState = hashtagTimelineUiState,
            statusUiState = statusUiState,
            messageFlow = viewModel.errorMessageFlow,
            onBackClick = navigator::pop,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onInteractive = viewModel::onInteractive,
            onFollowClick = viewModel::onFollowClick,
            onUnfollowClick = viewModel::onUnfollowClick,
        )
    }

    @OptIn(ExperimentalMotionApi::class)
    @Composable
    private fun HashtagTimelineContent(
        hashtagTimelineUiState: HashtagTimelineUiState,
        statusUiState: CommonLoadableUiState<StatusUiState>,
        messageFlow: SharedFlow<TextString>,
        onBackClick: () -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        val contentCanScrollBackward = remember {
            mutableStateOf(false)
        }
        CollapsableTopBarLayout(
            minTopBarHeight = ToolbarTokens.ContainerHeight,
            contentCanScrollBackward = contentCanScrollBackward,
            topBar = { collapsableProgress ->
                MotionLayout(
                    modifier = Modifier.fillMaxWidth(),
                    motionScene = buildMotionScene(),
                    progress = collapsableProgress,
                ) {
                    HashtagAppBar(
                        uiState = hashtagTimelineUiState,
                        onBackClick = onBackClick,
                        onFollowClick = onFollowClick,
                        onUnfollowClick = onUnfollowClick,
                    )
                }
            },
            scrollableContent = {
                Box(modifier = Modifier.fillMaxSize()) {
                    ActivityPubListStatusContent(
                        uiState = statusUiState,
                        onRefresh = onRefresh,
                        onLoadMore = onLoadMore,
                        onInteractive = onInteractive,
                    )
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 30.dp)
                    )
                }
            }
        )
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HashtagAppBar(
        uiState: HashtagTimelineUiState,
        onBackClick: () -> Unit,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
    ) {
        Surface(
            Modifier
                .layoutId("topBarBottomContent")
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1F),
                        style = MaterialTheme.typography.titleMedium,
                        text = uiState.hashTag,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                    )

                    FollowHashtagButton(
                        modifier = Modifier.padding(start = 8.dp),
                        uiState = uiState,
                        onFollowClick = onFollowClick,
                        onUnfollowClick = onUnfollowClick,
                    )
                }

                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = uiState.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                )
            }
        }
        TopAppBar(
            modifier = Modifier.layoutId("topBar"),
            navigationIcon = {
                Toolbar.BackButton(
                    onBackClick = onBackClick,
                )
            },
            title = {
                Text(
                    fontSize = 18.sp,
                    text = uiState.hashTag,
                )
            },
        )
    }

    @Composable
    private fun FollowHashtagButton(
        modifier: Modifier,
        uiState: HashtagTimelineUiState,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
    ) {
        var showUnfollowDialog by remember { mutableStateOf(false) }
        StyledTextButton(
            modifier = modifier,
            onClick = {
                if (uiState.following) {
                    showUnfollowDialog = true
                } else {
                    onFollowClick()
                }
            },
            style = if (uiState.following) {
                TextButtonStyle.STANDARD
            } else {
                TextButtonStyle.ACTIVE
            },
            text = if (uiState.following) {
                stringResource(R.string.activity_pub_user_detail_relationship_following)
            } else {
                stringResource(R.string.activity_pub_user_detail_relationship_not_follow)
            },
        )
        if (showUnfollowDialog) {
            AlertConfirmDialog(
                content = stringResource(R.string.activity_pub_hashtag_unfollow_dialog_message),
                onConfirm = onUnfollowClick,
                onDismissRequest = { showUnfollowDialog = false },
            )
        }
    }
}
