package com.zhangke.fread.activitypub.app.internal.screen.list.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.activitypub.app.internal.screen.list.ListDetailPageContent
import com.zhangke.fread.activitypub.app.internal.screen.user.search.SearchUserScreenNavKey
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.serialization.Serializable

@Serializable
data class AddListScreenNavKey(val locator: PlatformLocator) : NavKey

@Composable
fun AddListScreen(viewModel: AddListViewModel, locator: PlatformLocator) {
    val backStack = LocalNavBackStack.currentOrThrow
    val snackbarHostState = rememberSnackbarHostState()
    val uiState by viewModel.uiState.collectAsState()
    ConsumeFlow(SearchUserScreenNavKey.accountSelectedFlow.flow) {
        viewModel.onAddAccount(it)
    }
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
        onBackClick = { backStack.removeLastOrNull() },
        onRemoveAccount = viewModel::onRemoveAccount,
        onRetryLoadAccountsClick = {},
        onNameChangedRequest = viewModel::onNameChangeRequest,
        onAddUserClick = {
            backStack.add(SearchUserScreenNavKey(locator, onlyFollowing = false))
        },
    )
    ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarFlow)
    ConsumeFlow(viewModel.finishPageFlow) { backStack.removeLastOrNull() }
}
