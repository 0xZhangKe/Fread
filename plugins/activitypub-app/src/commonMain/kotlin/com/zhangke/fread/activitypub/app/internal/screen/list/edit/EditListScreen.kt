package com.zhangke.fread.activitypub.app.internal.screen.list.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.activitypub.app.internal.screen.list.ListDetailPageContent
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.IdentityRole

class EditListScreen(
    private val role: IdentityRole,
    private val serializedList: String,
) : BaseScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<EditListViewModel, EditListViewModel.Factory> {
            it.create(role, serializedList)
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarState = rememberSnackbarHostState()
        ListDetailPageContent(
            name = uiState.name,
            repliesPolicy = uiState.repliesPolicy,
            exclusive = uiState.exclusive,
            showLoadingCover = uiState.showLoadingCover,
            accountList = uiState.accountList,
            snackBarState = snackBarState,
            accountsLoading = uiState.accountsLoading,
            loadAccountsError = uiState.loadAccountsError,
            showSaveButton = false,
            onNameChangedRequest = { },
            onExclusiveChangeRequest = { },
            onRemoveAccount = { },
            onAddUserClick = {},
            onSaveClick = {},
            onBackClick = navigator::pop,
            onRetryLoadAccountsClick = viewModel::onRetryLoadAccountsClick,
            onPolicySelect = viewModel::onPolicySelect,
        )
    }

}
