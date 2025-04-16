package com.zhangke.fread.activitypub.app.internal.screen.list.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.activitypub.app.internal.screen.list.ListDetailPageContent
import com.zhangke.fread.activitypub.app.internal.screen.user.search.SearchUserScreen
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.IdentityRole

class AddListScreen(
    private val role: IdentityRole,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = rememberSnackbarHostState()
        val viewModel = getViewModel<AddListViewModel, AddListViewModel.Factory> {
            it.create(role)
        }
        val uiState by viewModel.uiState.collectAsState()
        ListDetailPageContent(
            name = uiState.name,
            snackBarState = snackbarHostState,
            exclusive = uiState.exclusive,
            repliesPolicy = uiState.repliesPolicy,
            showLoadingCover = uiState.showLoadingCover,
            accountList = uiState.accountList,
            accountsLoading = false,
            loadAccountsError = null,
            showDeleteIcon = false,
            onSaveClick = viewModel::onSaveClick,
            onExclusiveChangeRequest = viewModel::onExclusiveChanged,
            onPolicySelect = viewModel::onPolicySelect,
            onBackClick = navigator::pop,
            onRemoveAccount = viewModel::onRemoveAccount,
            onRetryLoadAccountsClick = {},
            onNameChangedRequest = viewModel::onNameChangeRequest,
            onAddUserClick = {
                navigator.push(SearchUserScreen(role, onlyFollowing = false).apply {
                    onAccountSelected = { account ->
                        viewModel.onAddAccount(account)
                    }
                })
            },
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarFlow)
        ConsumeFlow(viewModel.finishPageFlow) { navigator.pop() }
    }
}
