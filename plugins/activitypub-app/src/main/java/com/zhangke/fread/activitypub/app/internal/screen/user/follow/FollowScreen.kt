package com.zhangke.fread.activitypub.app.internal.screen.user.follow

import androidx.compose.foundation.layout.Box
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
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.screen.user.common.users.CommonUserPage
import com.zhangke.fread.activitypub.app.internal.screen.user.common.users.CommonUserUiState
import com.zhangke.fread.common.page.BaseScreen
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
        )
        ConsumeSnackbarFlow(snackBarHostState, viewModel.snackMessageFlow)
    }

    @Composable
    private fun FollowContent(
        uiState: CommonUserUiState,
        onBackClick: () -> Unit,
        snackBarHostState: SnackbarHostState,
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
                SnackbarHost(snackBarHostState)
            },
        ) { paddings ->
            Box(
                modifier = Modifier
                    .padding(paddings)
                    .fillMaxSize()
            ) {
                CommonUserPage(
                    uiState = uiState,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                    emptyText = stringResource(R.string.activity_pub_user_list_empty),
                )
            }
        }
    }
}
