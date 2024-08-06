package com.zhangke.fread.activitypub.app.internal.screen.user.follow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailRoute
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.common.CommonUserPlaceHolder
import com.zhangke.fread.activitypub.app.internal.screen.user.common.CommonUserUi
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.uri.FormalUri

class FollowScreen(
    private val role: IdentityRole,
    private val userUri: FormalUri,
    private val isFollowing: Boolean,
) : BaseScreen() {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<FollowViewModel, FollowViewModel.Factory> {
            it.create(role, userUri, isFollowing)
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = rememberSnackbarHostState()
        FollowContent(
            uiState = uiState,
            onBackClick = navigator::pop,
            snackBarHostState = snackBarHostState,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onAccountClick = {
                val route = UserDetailRoute.buildRoute(role, it.uri)
                navigator.push(UserDetailScreen(route))
            },
        )
        ConsumeSnackbarFlow(snackBarHostState, viewModel.messageFlow)
    }

    @Composable
    private fun FollowContent(
        uiState: FollowUiState,
        onBackClick: () -> Unit,
        snackBarHostState: SnackbarHostState,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onAccountClick: (BlogAuthor) -> Unit,
    ) {
        Scaffold(
            topBar = {
                val title = if (isFollowing) {
                    stringResource(R.string.activity_pub_user_following_list_title)
                } else {
                    stringResource(R.string.activity_pub_user_follower_list_title)
                }
                Toolbar(
                    title = title,
                    onBackClick = onBackClick,
                )
            },
            snackbarHost = {
                SnackbarHost(snackBarHostState)
            }
        ) { paddings ->
            if (uiState.initializing) {
                InitializingUi(Modifier.padding(paddings))
            } else {
                if (uiState.list.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "No users were obtained",
                        )
                    }
                } else {
                    val state = rememberLoadableLazyColumnState(
                        refreshing = uiState.refreshing,
                        onRefresh = onRefresh,
                        onLoadMore = onLoadMore,
                    )
                    LoadableLazyColumn(
                        modifier = Modifier
                            .padding(paddings)
                            .fillMaxSize(),
                        state = state,
                        refreshing = uiState.refreshing,
                        loadState = uiState.loadMoreState,
                    ) {
                        itemsIndexed(uiState.list) { index, account ->
                            FollowAccountUi(
                                account = account,
                                onClick = onAccountClick,
                                showDivider = index < uiState.list.lastIndex,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun InitializingUi(modifier: Modifier) {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(30) {
                CommonUserPlaceHolder()
            }
        }
    }

    @Composable
    private fun FollowAccountUi(
        account: BlogAuthor,
        onClick: (BlogAuthor) -> Unit,
        showDivider: Boolean,
    ) {
        CommonUserUi(
            modifier = Modifier.clickable {
                onClick(account)
            },
            user = account,
            showDivider = showDivider,
        )
    }
}
