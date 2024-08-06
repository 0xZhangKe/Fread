package com.zhangke.fread.activitypub.app.internal.screen.user.mute

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.screen.user.common.CommonUserPage
import com.zhangke.fread.activitypub.app.internal.screen.user.common.CommonUserUiState
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole

class MutedUserListScreen(
    private val role: IdentityRole,
) : BaseScreen() {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<MutedUserListViewModel, MutedUserListViewModel.Factory> {
            it.create(role)
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = rememberSnackbarHostState()
        MutedUserListContent(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onBackClick = navigator::pop,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onUnmuteClick = viewModel::onUnmuteClick,
        )
        ConsumeSnackbarFlow(snackBarHostState, viewModel.snackMessageFlow)
    }

    @Composable
    private fun MutedUserListContent(
        uiState: CommonUserUiState,
        snackBarHostState: SnackbarHostState,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onBackClick: () -> Unit,
        onUnmuteClick: (BlogAuthor) -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(R.string.activity_pub_muted_user_list_title),
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
                CommonUserPage(
                    uiState = uiState,
                    userAction = {
                        Spacer(modifier = Modifier.width(6.dp))
                        StyledTextButton(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            text = stringResource(R.string.activity_pub_muted_user_list_unmute),
                            style = TextButtonStyle.STANDARD,
                            onClick = {
                                onUnmuteClick(it)
                            },
                        )
                    },
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                    emptyText = stringResource(R.string.activity_pub_muted_user_list_empty),
                )
            }
        }
    }
}
