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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.StyledTextButton
import com.zhangke.framework.composable.TextButtonStyle
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.common.users.CommonUserPlaceHolder
import com.zhangke.fread.activitypub.app.internal.screen.user.common.users.CommonUserUi
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.uri.FormalUri
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router

@Destination(UserListRoute.ROUTE)
class UserListScreen(
    @Router private val route: String = "",
    private val role: IdentityRole? = null,
    private val type: UserListType? = null,
    private val statusId: String? = null,
    private val userUri: FormalUri? = null,
) : BaseScreen() {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<UserListViewModel, UserListViewModel.Factory> {
            if (route.isEmpty()) {
                it.create(role = role!!, type = type!!, statusId = statusId, userUri = userUri)
            } else {
                val (role, type, statusId) = UserListRoute.parseRouteAsReblogOrFavourited(route)!!
                it.create(role = role, type = type, statusId = statusId, userUri = null)
            }
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = rememberSnackbarHostState()
        UserListContent(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onUnblockClick = viewModel::onUnblockClick,
            onUnmuteClick = viewModel::onUnmuteClick,
            onBackClick = navigator::pop,
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
    ) {
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
                val navigator = LocalNavigator.currentOrThrow
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
                                    navigator.push(
                                        UserDetailScreen(
                                            role = uiState.role,
                                            webFinger = item.webFinger,
                                        )
                                    )
                                },
                                user = item,
                                showDivider = index < uiState.userList.lastIndex,
                                actionButton = {
                                    StatusAction(
                                        author = item,
                                        onUnblockClick = onUnblockClick,
                                        onUnmuteClick = onUnmuteClick,
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
                            text = stringResource(R.string.activity_pub_user_list_empty),
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.StatusAction(
        author: BlogAuthor,
        onUnblockClick: (BlogAuthor) -> Unit,
        onUnmuteClick: (BlogAuthor) -> Unit,
    ) {
        when (type) {
            UserListType.BLOCKED -> {
                Spacer(modifier = Modifier.width(6.dp))
                StyledTextButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(R.string.activity_pub_blocked_user_list_unblock),
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
                    text = stringResource(R.string.activity_pub_muted_user_list_unmute),
                    style = TextButtonStyle.STANDARD,
                    onClick = {
                        onUnmuteClick(author)
                    },
                )
            }

            else -> {}
        }
    }

    private val UserListType.title: String
        @Composable get() = when (this) {
            UserListType.FAVOURITES -> stringResource(R.string.activity_pub_status_favourites_by_title)
            UserListType.REBLOGS -> stringResource(R.string.activity_pub_status_reblog_by_title)
            UserListType.MUTED -> stringResource(R.string.activity_pub_muted_user_list_title)
            UserListType.BLOCKED -> stringResource(R.string.activity_pub_blocked_user_list_title)
            UserListType.FOLLOWERS -> stringResource(R.string.activity_pub_user_follower_list_title)
            UserListType.FOLLOWING -> stringResource(R.string.activity_pub_user_following_list_title)
        }
}
