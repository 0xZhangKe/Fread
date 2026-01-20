package com.zhangke.fread.bluesky.internal.screen.user.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.DefaultEmpty
import com.zhangke.framework.composable.DefaultFailed
import com.zhangke.framework.composable.StyledTextButton
import com.zhangke.framework.composable.TextButtonStyle
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.textString
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailScreenNavKey
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.user.CommonUserPlaceHolder
import com.zhangke.fread.status.ui.user.CommonUserUi
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class UserListScreenNavKey(
    val locator: PlatformLocator,
    val type: UserListType,
    val postUri: String? = null,
    val did: String? = null,
) : NavKey

@Composable
fun UserListScreen(
    locator: PlatformLocator,
    type: UserListType,
    viewModel: UserListViewModel,
) {
    val backStack = LocalNavBackStack.currentOrThrow
    val snackbarHostState = rememberSnackbarHostState()
    val uiState by viewModel.uiState.collectAsState()

    UserListContent(
        type = type,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = backStack::removeLastOrNull,
        onRefresh = viewModel::onRefresh,
        onLoadMore = viewModel::onLoadMore,
        onFollowClick = viewModel::onFollowClick,
        onUnfollowClick = viewModel::onUnfollowClick,
        onMuteClick = viewModel::onMuteClick,
        onUnmuteClick = viewModel::onUnmuteClick,
        onBlockClick = viewModel::onBlockClick,
        onUnblockClick = viewModel::onUnblockClick,
        onUserClick = { backStack.add(BskyUserDetailScreenNavKey(locator, it.did)) },
    )
    ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarMessage)
}

@Composable
private fun UserListContent(
    type: UserListType,
    uiState: CommonLoadableUiState<UserListItemUiState>,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onFollowClick: (UserListItemUiState) -> Unit,
    onUnfollowClick: (UserListItemUiState) -> Unit,
    onMuteClick: (UserListItemUiState) -> Unit,
    onUnmuteClick: (UserListItemUiState) -> Unit,
    onBlockClick: (UserListItemUiState) -> Unit,
    onUnblockClick: (UserListItemUiState) -> Unit,
    onUserClick: (UserListItemUiState) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = type.title,
                onBackClick = onBackClick,
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (uiState.dataList.isEmpty() && uiState.initializing) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(30) { CommonUserPlaceHolder() }
                }
            } else if (uiState.dataList.isEmpty() && uiState.errorMessage != null) {
                DefaultFailed(
                    modifier = Modifier.fillMaxSize(),
                    errorMessage = textString(uiState.errorMessage!!),
                    onRetryClick = onRefresh,
                )
            } else {
                val loadableState = rememberLoadableLazyColumnState(
                    refreshing = uiState.refreshing,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                )
                LoadableLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = loadableState,
                    refreshing = uiState.refreshing,
                    loadState = uiState.loadMoreState,
                ) {
                    if (uiState.dataList.isNotEmpty()) {
                        itemsIndexed(uiState.dataList) { index, item ->
                            CommonUserUi(
                                modifier = Modifier.clickable { onUserClick(item) },
                                user = item.author,
                                showDivider = index < uiState.dataList.lastIndex,
                                actionButton = {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    UserAction(
                                        type = type,
                                        user = item,
                                        onFollowClick = onFollowClick,
                                        onUnfollowClick = onUnfollowClick,
                                        onMuteClick = onMuteClick,
                                        onUnmuteClick = onUnmuteClick,
                                        onBlockClick = onBlockClick,
                                        onUnblockClick = onUnblockClick,
                                    )
                                },
                            )
                        }
                    } else {
                        item { DefaultEmpty() }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.UserAction(
    type: UserListType,
    user: UserListItemUiState,
    onFollowClick: (UserListItemUiState) -> Unit,
    onUnfollowClick: (UserListItemUiState) -> Unit,
    onMuteClick: (UserListItemUiState) -> Unit,
    onUnmuteClick: (UserListItemUiState) -> Unit,
    onBlockClick: (UserListItemUiState) -> Unit,
    onUnblockClick: (UserListItemUiState) -> Unit,
) {
    when (type) {
        UserListType.MUTED -> {
            var showConfirmDialog by remember { mutableStateOf(false) }
            StyledTextButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = if (user.muted) {
                    stringResource(LocalizedString.sharedUserListActionMuted)
                } else {
                    stringResource(LocalizedString.sharedUserListActionMute)
                },
                style = if (user.muted) TextButtonStyle.STANDARD else TextButtonStyle.ALERT,
                onClick = {
                    if (user.muted) {
                        onUnmuteClick(user)
                    } else {
                        showConfirmDialog = true
                    }
                },
            )
            if (showConfirmDialog) {
                AlertConfirmDialog(
                    content = stringResource(LocalizedString.sharedUserListActionMuteDialogMessage),
                    onConfirm = { onMuteClick(user) },
                    onDismissRequest = { showConfirmDialog = false },
                )
            }
        }

        UserListType.BLOCKED -> {
            var showConfirmDialog by remember { mutableStateOf(false) }
            StyledTextButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = if (user.blocked) {
                    stringResource(LocalizedString.sharedUserListActionBlocked)
                } else {
                    stringResource(LocalizedString.sharedUserListActionBlock)
                },
                style = if (user.blocked) TextButtonStyle.STANDARD else TextButtonStyle.ALERT,
                onClick = {
                    if (user.blocked) {
                        onUnblockClick(user)
                    } else {
                        showConfirmDialog = false
                    }
                },
            )
            if (showConfirmDialog) {
                AlertConfirmDialog(
                    content = stringResource(LocalizedString.sharedUserListActionBlockDialogMessage),
                    onConfirm = { onBlockClick(user) },
                    onDismissRequest = { showConfirmDialog = false },
                )
            }
        }

        else -> {
            var showConfirmDialog by remember { mutableStateOf(false) }
            StyledTextButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = if (user.following) {
                    stringResource(LocalizedString.statusUiUserDetailRelationshipFollowing)
                } else {
                    stringResource(LocalizedString.statusUiUserDetailRelationshipNotFollow)
                },
                style = TextButtonStyle.STANDARD,
                onClick = {
                    if (user.following) {
                        showConfirmDialog = false
                    } else {
                        onFollowClick(user)
                    }
                },
            )
            if (showConfirmDialog) {
                AlertConfirmDialog(
                    content = stringResource(LocalizedString.statusUiRelationshipBtnDialogContentCancelFollow),
                    onConfirm = { onUnfollowClick(user) },
                    onDismissRequest = { showConfirmDialog = false },
                )
            }
        }
    }
}

private val UserListType.title: String
    @Composable get() = when (this) {
        UserListType.LIKE -> stringResource(LocalizedString.sharedUserListTitleLikes)
        UserListType.REBLOG -> stringResource(LocalizedString.sharedUserListTitleReblog)
        UserListType.MUTED -> stringResource(LocalizedString.sharedUserListTitleMutes)
        UserListType.BLOCKED -> stringResource(LocalizedString.sharedUserListTitleBlocks)
        UserListType.FOLLOWERS -> stringResource(LocalizedString.sharedUserListTitleFollowers)
        UserListType.FOLLOWING -> stringResource(LocalizedString.sharedUserListTitleFollowing)
    }
