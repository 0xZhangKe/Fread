package com.zhangke.fread.bluesky.internal.screen.user.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreenNavKey
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.embed.BlogInEmbedding
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
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
    val quotesUiState by viewModel.quotesUiState.collectAsState()
    val mode by viewModel.mode.collectAsState()

    UserListContent(
        type = type,
        uiState = uiState,
        quotesUiState = quotesUiState,
        mode = mode,
        showModeSelector = type == UserListType.REBLOG,
        snackbarHostState = snackbarHostState,
        onBackClick = backStack::removeLastOrNull,
        onModeChange = viewModel::onModeChange,
        onRefresh = viewModel::onRefresh,
        onLoadMore = viewModel::onLoadMore,
        onFollowClick = viewModel::onFollowClick,
        onUnfollowClick = viewModel::onUnfollowClick,
        onMuteClick = viewModel::onMuteClick,
        onUnmuteClick = viewModel::onUnmuteClick,
        onBlockClick = viewModel::onBlockClick,
        onUnblockClick = viewModel::onUnblockClick,
        onUserClick = { backStack.add(BskyUserDetailScreenNavKey(locator, it.did)) },
        onQuoteClick = { blog ->
            backStack.add(StatusContextScreenNavKey.create(locator, blog))
        },
    )
    ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarMessage)
}

@Composable
private fun UserListContent(
    type: UserListType,
    uiState: CommonLoadableUiState<UserListItemUiState>,
    quotesUiState: CommonLoadableUiState<Blog>,
    mode: UserListViewModel.Mode,
    showModeSelector: Boolean,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onModeChange: (UserListViewModel.Mode) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onFollowClick: (UserListItemUiState) -> Unit,
    onUnfollowClick: (UserListItemUiState) -> Unit,
    onMuteClick: (UserListItemUiState) -> Unit,
    onUnmuteClick: (UserListItemUiState) -> Unit,
    onBlockClick: (UserListItemUiState) -> Unit,
    onUnblockClick: (UserListItemUiState) -> Unit,
    onUserClick: (UserListItemUiState) -> Unit,
    onQuoteClick: (Blog) -> Unit,
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
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (showModeSelector) {
                BoostsQuotesSelector(
                    mode = mode,
                    onModeChange = onModeChange,
                )
            }
            Box(modifier = Modifier.fillMaxSize()) {
                if (mode == UserListViewModel.Mode.QUOTES) {
                    QuotesList(
                        uiState = quotesUiState,
                        onRefresh = onRefresh,
                        onLoadMore = onLoadMore,
                        onQuoteClick = onQuoteClick,
                    )
                } else {
                    BoostsList(
                        type = type,
                        uiState = uiState,
                        onRefresh = onRefresh,
                        onLoadMore = onLoadMore,
                        onUserClick = onUserClick,
                        onFollowClick = onFollowClick,
                        onUnfollowClick = onUnfollowClick,
                        onMuteClick = onMuteClick,
                        onUnmuteClick = onUnmuteClick,
                        onBlockClick = onBlockClick,
                        onUnblockClick = onUnblockClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun BoostsQuotesSelector(
    mode: UserListViewModel.Mode,
    onModeChange: (UserListViewModel.Mode) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        MultiChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(0.7F),
        ) {
            SegmentedButton(
                checked = mode == UserListViewModel.Mode.BOOSTS,
                onCheckedChange = { onModeChange(UserListViewModel.Mode.BOOSTS) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            ) {
                Text(text = stringResource(LocalizedString.bluesky_user_list_tab_boosts))
            }
            SegmentedButton(
                checked = mode == UserListViewModel.Mode.QUOTES,
                onCheckedChange = { onModeChange(UserListViewModel.Mode.QUOTES) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text(text = stringResource(LocalizedString.bluesky_user_list_tab_quotes))
            }
        }
    }
}

@Composable
private fun BoxScope.BoostsList(
    type: UserListType,
    uiState: CommonLoadableUiState<UserListItemUiState>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onUserClick: (UserListItemUiState) -> Unit,
    onFollowClick: (UserListItemUiState) -> Unit,
    onUnfollowClick: (UserListItemUiState) -> Unit,
    onMuteClick: (UserListItemUiState) -> Unit,
    onUnmuteClick: (UserListItemUiState) -> Unit,
    onBlockClick: (UserListItemUiState) -> Unit,
    onUnblockClick: (UserListItemUiState) -> Unit,
) {
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

@Composable
private fun BoxScope.QuotesList(
    uiState: CommonLoadableUiState<Blog>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onQuoteClick: (Blog) -> Unit,
) {
    if (uiState.dataList.isEmpty() && uiState.initializing) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(8) { CommonUserPlaceHolder() }
        }
    } else if (uiState.dataList.isEmpty() && uiState.errorMessage != null) {
        DefaultFailed(
            modifier = Modifier.fillMaxSize(),
            errorMessage = textString(uiState.errorMessage!!),
            onRetryClick = onRefresh,
        )
    } else {
        val loadableState = rememberLoadableLazyColumnState(
            onRefresh = onRefresh,
            onLoadMore = onLoadMore,
        )
        val style = LocalStatusUiConfig.current.contentStyle
        LoadableLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = loadableState,
            refreshing = uiState.refreshing,
            loadState = uiState.loadMoreState,
        ) {
            if (uiState.dataList.isNotEmpty()) {
                itemsIndexed(uiState.dataList) { index, blog ->
                    BlogInEmbedding(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        blog = blog,
                        style = style,
                        onContentClick = onQuoteClick,
                    )
                    if (index < uiState.dataList.lastIndex) {
                        HorizontalDivider()
                    }
                }
            } else {
                item { DefaultEmpty() }
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
