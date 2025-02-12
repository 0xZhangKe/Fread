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
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.DefaultEmpty
import com.zhangke.framework.composable.DefaultFailed
import com.zhangke.framework.composable.StyledTextButton
import com.zhangke.framework.composable.TextButtonStyle
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.textString
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailScreen
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_action_block
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_action_block_dialog_message
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_action_blocked
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_action_mute
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_action_mute_dialog_message
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_action_muted
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_title_blocks
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_title_followers
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_title_following
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_title_likes
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_title_mutes
import com.zhangke.fread.commonbiz.shared.screen.shared_user_list_title_reblog
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.user.CommonUserPlaceHolder
import com.zhangke.fread.status.ui.user.CommonUserUi
import com.zhangke.fread.statusui.status_ui_relationship_btn_dialog_content_cancel_follow
import com.zhangke.fread.statusui.status_ui_user_detail_relationship_following
import com.zhangke.fread.statusui.status_ui_user_detail_relationship_not_follow
import org.jetbrains.compose.resources.stringResource
import com.zhangke.fread.statusui.Res as StatusRes

class UserListScreen(
    private val role: IdentityRole,
    private val type: UserListType,
    private val postUri: String? = null,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = rememberSnackbarHostState()
        val viewModel = getViewModel<UserListViewModel, UserListViewModel.Factory>() {
            it.create(role, type, postUri)
        }
        val uiState by viewModel.uiState.collectAsState()

        UserListContent(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onBackClick = navigator::pop,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onFollowClick = viewModel::onFollowClick,
            onUnfollowClick = viewModel::onUnfollowClick,
            onMuteClick = viewModel::onMuteClick,
            onUnmuteClick = viewModel::onUnmuteClick,
            onBlockClick = viewModel::onBlockClick,
            onUnblockClick = viewModel::onUnblockClick,
            onUserClick = { navigator.push(BskyUserDetailScreen(role, it.did)) },
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarMessage)
    }

    @Composable
    private fun UserListContent(
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
                        stringResource(Res.string.shared_user_list_action_muted)
                    } else {
                        stringResource(Res.string.shared_user_list_action_mute)
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
                        content = stringResource(Res.string.shared_user_list_action_mute_dialog_message),
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
                        stringResource(Res.string.shared_user_list_action_blocked)
                    } else {
                        stringResource(Res.string.shared_user_list_action_block)
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
                        content = stringResource(Res.string.shared_user_list_action_block_dialog_message),
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
                        stringResource(StatusRes.string.status_ui_user_detail_relationship_following)
                    } else {
                        stringResource(StatusRes.string.status_ui_user_detail_relationship_not_follow)
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
                        content = stringResource(StatusRes.string.status_ui_relationship_btn_dialog_content_cancel_follow),
                        onConfirm = { onUnfollowClick(user) },
                        onDismissRequest = { showConfirmDialog = false },
                    )
                }
            }
        }
    }

    private val UserListType.title: String
        @Composable get() = when (this) {
            UserListType.LIKE -> stringResource(Res.string.shared_user_list_title_likes)
            UserListType.REBLOG -> stringResource(Res.string.shared_user_list_title_reblog)
            UserListType.MUTED -> stringResource(Res.string.shared_user_list_title_mutes)
            UserListType.BLOCKED -> stringResource(Res.string.shared_user_list_title_blocks)
            UserListType.FOLLOWERS -> stringResource(Res.string.shared_user_list_title_followers)
            UserListType.FOLLOWING -> stringResource(Res.string.shared_user_list_title_following)
        }
}
