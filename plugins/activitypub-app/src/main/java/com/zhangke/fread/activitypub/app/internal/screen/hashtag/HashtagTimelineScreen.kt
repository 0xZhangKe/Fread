package com.zhangke.fread.activitypub.app.internal.screen.hashtag

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.constraintlayout.compose.MotionScene
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.StyledTextButton
import com.zhangke.framework.composable.TextButtonStyle
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.composable.ScrollUpTopBarLayout
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.commonbiz.shared.feeds.CommonFeedsUiState
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.RouteParam
import kotlinx.coroutines.flow.SharedFlow
import java.net.URLDecoder

@Destination(HashtagTimelineRoute.ROUTE)
data class HashtagTimelineScreen(
    @RouteParam(HashtagTimelineRoute.PARAMS_ROLE) private val _role: String? = null,
    @RouteParam(HashtagTimelineRoute.PARAM_HASHTAG) private val _hashtag: String? = null,
    private val role: IdentityRole = IdentityRole.decodeFromString(_role!!)!!,
    private val hashtag: String = URLDecoder.decode(_hashtag!!, Charsets.UTF_8.name())
) : BaseScreen() {

    override val key: ScreenKey
        get() = _role + _hashtag
    
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            getViewModel<HashtagTimelineContainerViewModel>().getViewModel(role, hashtag)
        val hashtagTimelineUiState by viewModel.hashtagTimelineUiState.collectAsState()
        val statusUiState by viewModel.uiState.collectAsState()
        HashtagTimelineContent(
            hashtagTimelineUiState = hashtagTimelineUiState,
            statusUiState = statusUiState,
            messageFlow = viewModel.errorMessageFlow,
            openScreenFlow = viewModel.openScreenFlow,
            newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
            onBackClick = navigator::pop,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            onFollowClick = viewModel::onFollowClick,
            onUnfollowClick = viewModel::onUnfollowClick,
        )
        ConsumeSnackbarFlow(LocalSnackbarHostState.current, viewModel.errorMessageFlow)
    }

    @Composable
    private fun HashtagTimelineContent(
        hashtagTimelineUiState: HashtagTimelineUiState,
        statusUiState: CommonFeedsUiState,
        messageFlow: SharedFlow<TextString>,
        openScreenFlow: SharedFlow<Screen>,
        newStatusNotifyFlow: SharedFlow<Unit>,
        onBackClick: () -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        composedStatusInteraction: ComposedStatusInteraction,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        val contentCanScrollBackward = remember {
            mutableStateOf(false)
        }
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPadding ->
            ScrollUpTopBarLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                immersiveToTopBar = false,
                topBarContent = {
                    Toolbar(
                        title = hashtagTimelineUiState.hashTag,
                        onBackClick = onBackClick,
                    )
                },
                headerContent = {
                    HashtagHeaderContent(
                        uiState = hashtagTimelineUiState,
                        onFollowClick = onFollowClick,
                        onUnfollowClick = onUnfollowClick,
                    )
                },
                contentCanScrollBackward = contentCanScrollBackward,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    FeedsContent(
                        uiState = statusUiState,
                        openScreenFlow = openScreenFlow,
                        newStatusNotifyFlow = newStatusNotifyFlow,
                        composedStatusInteraction = composedStatusInteraction,
                        onRefresh = onRefresh,
                        onLoadMore = onLoadMore,
                        nestedScrollConnection = null,
                        contentCanScrollBackward = contentCanScrollBackward,
                    )
                }
            }
        }
        ConsumeSnackbarFlow(snackbarHostState, messageFlow)
    }

    @Composable
    private fun HashtagHeaderContent(
        uiState: HashtagTimelineUiState,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
    ) {
        Surface(
            Modifier.fillMaxWidth()
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
    }

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
        transition(name = "default", from = start1, to = end1) {}
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
