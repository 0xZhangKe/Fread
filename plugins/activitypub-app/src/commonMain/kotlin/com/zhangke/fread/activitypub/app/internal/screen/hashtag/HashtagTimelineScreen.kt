package com.zhangke.fread.activitypub.app.internal.screen.hashtag

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.plusContentPadding
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.commonbiz.shared.feeds.CommonFeedsUiState
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class HashtagTimelineScreenKey(
    val locator: PlatformLocator,
    val hashtag: String,
) : NavKey

@Composable
fun HashtagTimelineScreen(
    viewModel: HashtagTimelineContainerViewModel,
    locator: PlatformLocator,
    hashtag: String,
) {
    val backStack = LocalNavBackStack.currentOrThrow
    val viewModel = viewModel.getViewModel(locator, hashtag)
    val hashtagTimelineUiState by viewModel.hashtagTimelineUiState.collectAsState()
    val statusUiState by viewModel.uiState.collectAsState()
    HashtagTimelineContent(
        hashtagTimelineUiState = hashtagTimelineUiState,
        statusUiState = statusUiState,
        messageFlow = viewModel.errorMessageFlow,
        openScreenFlow = viewModel.openScreenFlow,
        newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
        onBackClick = backStack::removeLastOrNull,
        onRefresh = viewModel::onRefresh,
        onLoadMore = viewModel::onLoadMore,
        composedStatusInteraction = viewModel.composedStatusInteraction,
        onFollowClick = viewModel::onFollowClick,
        onUnfollowClick = viewModel::onUnfollowClick,
    )
    ConsumeSnackbarFlow(LocalSnackbarHostState.current, viewModel.errorMessageFlow)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HashtagTimelineContent(
    hashtagTimelineUiState: HashtagTimelineUiState,
    statusUiState: CommonFeedsUiState,
    messageFlow: SharedFlow<TextString>,
    openScreenFlow: SharedFlow<NavKey>,
    newStatusNotifyFlow: SharedFlow<Unit>,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    composedStatusInteraction: ComposedStatusInteraction,
    onFollowClick: () -> Unit,
    onUnfollowClick: () -> Unit,
) {
    val snackbarHostState = rememberSnackbarHostState()
    val topBarState = rememberTopAppBarState()
    val topBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)
    val collapsedFraction = topBarScrollBehavior.state.collapsedFraction.coerceIn(0F, 1F)
    val subtitleAlpha by animateFloatAsState(
        targetValue = (1F - collapsedFraction * 1.4F).coerceIn(0F, 1F),
        label = "hashtag_subtitle_alpha",
    )
    val followButtonScale by animateFloatAsState(
        targetValue = 1F - 0.08F * collapsedFraction,
        label = "hashtag_follow_button_scale",
    )
    Scaffold(
        modifier = Modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            LargeTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                modifier = Modifier.weight(1F),
                                text = hashtagTimelineUiState.hashTag,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            FollowHashtagButton(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .graphicsLayer {
                                        scaleX = followButtonScale
                                        scaleY = followButtonScale
                                    },
                                uiState = hashtagTimelineUiState,
                                onFollowClick = onFollowClick,
                                onUnfollowClick = onUnfollowClick,
                            )
                        }
                        if (hashtagTimelineUiState.description.isNotBlank()) {
                            Text(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .graphicsLayer { alpha = subtitleAlpha },
                                text = hashtagTimelineUiState.description,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = {
                    Toolbar.BackButton(onBackClick = onBackClick)
                },
                scrollBehavior = topBarScrollBehavior,
            )
        },
    ) { innerPadding ->
        CompositionLocalProvider(
            LocalContentPadding provides plusContentPadding(innerPadding),
        ) {
            FeedsContent(
                uiState = statusUiState,
                openScreenFlow = openScreenFlow,
                newStatusNotifyFlow = newStatusNotifyFlow,
                composedStatusInteraction = composedStatusInteraction,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
                nestedScrollConnection = null,
            )
        }
    }
    ConsumeSnackbarFlow(snackbarHostState, messageFlow)
}

@Composable
private fun FollowHashtagButton(
    modifier: Modifier,
    uiState: HashtagTimelineUiState,
    onFollowClick: () -> Unit,
    onUnfollowClick: () -> Unit,
) {
    var showUnfollowDialog by remember { mutableStateOf(false) }
    FilledTonalButton(
        modifier = modifier,
        onClick = {
            if (uiState.following) {
                showUnfollowDialog = true
            } else {
                onFollowClick()
            }
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (uiState.following) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            },
        ),
    ) {
        Text(
            text = if (uiState.following) {
                stringResource(LocalizedString.statusUiUserDetailRelationshipFollowing)
            } else {
                stringResource(LocalizedString.statusUiUserDetailRelationshipNotFollow)
            },
        )
    }
    if (showUnfollowDialog) {
        AlertConfirmDialog(
            content = stringResource(LocalizedString.activity_pub_hashtag_unfollow_dialog_message),
            onConfirm = onUnfollowClick,
            onDismissRequest = { showUnfollowDialog = false },
        )
    }
}
