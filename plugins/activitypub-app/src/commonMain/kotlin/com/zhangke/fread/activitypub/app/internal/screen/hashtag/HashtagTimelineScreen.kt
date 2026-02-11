package com.zhangke.fread.activitypub.app.internal.screen.hashtag

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.blur.BlurController
import com.zhangke.framework.blur.LocalBlurController
import com.zhangke.framework.blur.applyBlurEffect
import com.zhangke.framework.blur.blurEffectContainerColor
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.SingleRowTopAppBar
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.TopAppBarColors
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
    val topBarColor = MaterialTheme.colorScheme.surface
    val blurController = remember { BlurController.create() }
    CompositionLocalProvider(
        LocalBlurController provides blurController
    ) {
        Scaffold(
            modifier = Modifier,
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            topBar = {
                SingleRowTopAppBar(
                    modifier = Modifier.fillMaxWidth()
                        .applyBlurEffect(containerColor = topBarColor),
                    colors = TopAppBarColors.default(
                        containerColor = blurEffectContainerColor(containerColor = topBarColor),
                    ),
                    title = {
                        Column {
                            Text(
                                hashtagTimelineUiState.hashTag,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                modifier = Modifier
                                    .padding(top = 2.dp),
                                text = hashtagTimelineUiState.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    navigationIcon = {
                        Toolbar.BackButton(onBackClick = onBackClick)
                    },
                    actions = {
                        FollowHashtagButton(
                            modifier = Modifier.padding(end = 8.dp),
                            uiState = hashtagTimelineUiState,
                            onFollowClick = onFollowClick,
                            onUnfollowClick = onUnfollowClick,
                        )
                    },
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
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
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
