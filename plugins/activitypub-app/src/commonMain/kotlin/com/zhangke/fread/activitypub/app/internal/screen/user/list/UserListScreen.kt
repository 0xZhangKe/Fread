package com.zhangke.fread.activitypub.app.internal.screen.user.list

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.StyledTextButton
import com.zhangke.framework.composable.TextButtonStyle
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreenKey
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.user.CommonUserPlaceHolder
import com.zhangke.fread.status.ui.user.CommonUserUi
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class UserListScreenKey(
    val locator: PlatformLocator,
    val type: UserListType,
    val statusId: String? = null,
    val userUri: FormalUri? = null,
    val userId: String? = null,
) : NavKey

@Composable
fun UserListScreen(viewModel: UserListViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = rememberSnackbarHostState()
    UserListContent(
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onRefresh = viewModel::onRefresh,
        onLoadMore = viewModel::onLoadMore,
        onUnblockClick = viewModel::onUnblockClick,
        onUnmuteClick = viewModel::onUnmuteClick,
        onBackClick = backStack::removeLastOrNull,
        onFollowClick = viewModel::onFollowClick,
    )
    ConsumeSnackbarFlow(snackBarHostState, viewModel.snackMessageFlow)
}

@Composable
private fun UserListContent(
    uiState: UserListUiState,
    snackBarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onUnblockClick: (BlogAuthor) -> Unit,
    onUnmuteClick: (BlogAuthor) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onFollowClick: (BlogAuthorUiState) -> Unit,
) {
    val backStack = LocalNavBackStack.currentOrThrow
    Scaffold(
        topBar = {
            Toolbar(
                title = uiState.type.title,
                onBackClick = onBackClick,
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.userList.isNotEmpty()) {
                val loadableState = rememberLoadableLazyColumnState(
                    refreshing = uiState.loading,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                )
                LoadableLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = loadableState,
                    refreshing = uiState.loading,
                    loadState = uiState.loadMoreState,
                ) {
                    itemsIndexed(uiState.userList) { index, item ->
                        CommonUserUi(
                            modifier = Modifier.clickable {
                                backStack.add(
                                    UserDetailScreenKey(
                                        locator = uiState.locator,
                                        webFinger = item.author.webFinger,
                                        userId = item.author.userId,
                                    )
                                )
                            },
                            user = item.author,
                            showDivider = index < uiState.userList.lastIndex,
                            actionButton = {
                                StatusAction(
                                    authorUiState = item,
                                    type = uiState.type,
                                    onUnblockClick = onUnblockClick,
                                    onUnmuteClick = onUnmuteClick,
                                    onFollowClick = onFollowClick,
                                )
                            },
                        )
                    }
                }
            } else if (uiState.loading) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(30) {
                        CommonUserPlaceHolder()
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(LocalizedString.activity_pub_user_list_empty),
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.StatusAction(
    authorUiState: BlogAuthorUiState,
    type: UserListType,
    onUnblockClick: (BlogAuthor) -> Unit,
    onUnmuteClick: (BlogAuthor) -> Unit,
    onFollowClick: (BlogAuthorUiState) -> Unit,
) {
    val author = authorUiState.author
    when (type) {
        UserListType.BLOCKED -> {
            Spacer(modifier = Modifier.width(6.dp))
            StyledTextButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(LocalizedString.sharedUserListActionBlocked),
                style = TextButtonStyle.STANDARD,
                onClick = {
                    onUnblockClick(author)
                },
            )
        }

        UserListType.MUTED -> {
            Spacer(modifier = Modifier.width(6.dp))
            StyledTextButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(LocalizedString.sharedUserListActionMuted),
                style = TextButtonStyle.STANDARD,
                onClick = {
                    onUnmuteClick(author)
                },
            )
        }

        UserListType.REBLOGS, UserListType.FOLLOWERS, UserListType.FAVOURITES -> {
            if (authorUiState.following == false) {
                Spacer(modifier = Modifier.width(6.dp))
                StyledTextButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(LocalizedString.statusUiFollow),
                    style = TextButtonStyle.STANDARD,
                    onClick = {
                        onFollowClick(authorUiState)
                    },
                )
            }
        }

        else -> {}
    }
}

private val UserListType.title: String
    @Composable get() = when (this) {
        UserListType.FAVOURITES -> stringResource(LocalizedString.sharedUserListTitleLikes)
        UserListType.REBLOGS -> stringResource(LocalizedString.sharedUserListTitleReblog)
        UserListType.MUTED -> stringResource(LocalizedString.sharedUserListTitleMutes)
        UserListType.BLOCKED -> stringResource(LocalizedString.sharedUserListTitleBlocks)
        UserListType.FOLLOWERS -> stringResource(LocalizedString.sharedUserListTitleFollowers)
        UserListType.FOLLOWING -> stringResource(LocalizedString.sharedUserListTitleFollowing)
    }
