package com.zhangke.utopia.activitypub.app.internal.screen.user.follow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.status.uri.FormalUri

class FollowScreen(private val userUri: FormalUri, private val isFollowing: Boolean) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<FollowViewModel, FollowViewModel.Factory> {
            it.create(userUri, isFollowing)
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = rememberSnackbarHostState()
        FollowContent(
            uiState = uiState,
            onBackClick = navigator::pop,
            snackbarHostState = snackbarHostState,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.messageFlow)
    }

    @Composable
    private fun FollowContent(
        uiState: FollowUiState,
        onBackClick: () -> Unit,
        snackbarHostState: SnackbarHostState,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
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
                SnackbarHost(snackbarHostState)
            }
        ) { paddings ->
            val state = rememberLoadableLazyColumnState(
                refreshing = uiState.refreshing,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
            )
            LoadableLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings),
                state = state,
                refreshing = uiState.refreshing,
                loadState = uiState.loadMoreState,
            ) {

            }
        }
    }
}
