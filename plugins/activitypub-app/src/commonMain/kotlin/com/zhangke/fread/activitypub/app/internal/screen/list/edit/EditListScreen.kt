package com.zhangke.fread.activitypub.app.internal.screen.list.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.activitypub.app.internal.screen.list.ListDetailPageContent
import com.zhangke.fread.activitypub.app.internal.screen.user.search.SearchUserScreen
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator

class EditListScreen(
    private val locator: PlatformLocator,
    private val serializedList: String,
) : BaseScreen() {

    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<EditListViewModel, EditListViewModel.Factory> {
            it.create(locator, serializedList)
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarState = rememberSnackbarHostState()
        var showBackReminder by remember { mutableStateOf(false) }
        fun onBack() {
            if (uiState.contentHasChanged) {
                showBackReminder = true
            } else {
                navigator.pop()
            }
        }
        BackHandler(true) { onBack() }
        if (showBackReminder) {
            FreadDialog(
                onDismissRequest = { showBackReminder = false },
                contentText = org.jetbrains.compose.resources.stringResource(LocalizedString.activity_pub_add_list_back_reminder),
                onNegativeClick = { showBackReminder = false },
                onPositiveClick = {
                    showBackReminder = false
                    navigator.pop()
                },
            )
        }
        ListDetailPageContent(
            name = uiState.name,
            repliesPolicy = uiState.repliesPolicy,
            exclusive = uiState.exclusive,
            showLoadingCover = uiState.showLoadingCover,
            accountList = uiState.accountList,
            snackBarState = snackBarState,
            showDeleteIcon = true,
            accountsLoading = uiState.accountsLoading,
            loadAccountsError = uiState.loadAccountsError,
            onNameChangedRequest = viewModel::onNameChangeRequest,
            onExclusiveChangeRequest = viewModel::onExclusiveChanged,
            onRemoveAccount = viewModel::onRemoveAccount,
            onAddUserClick = {
                val searchUserScreen = SearchUserScreen(locator, true)
                searchUserScreen.onAccountSelected = viewModel::onAddUser
                navigator.push(searchUserScreen)
            },
            onSaveClick = viewModel::onSaveClick,
            onBackClick = { onBack() },
            onRetryLoadAccountsClick = viewModel::onRetryLoadAccountsClick,
            onPolicySelect = viewModel::onPolicySelect,
            onDeleteClick = viewModel::onDeleteClick,
        )
        ConsumeSnackbarFlow(snackBarState, viewModel.snackBarFlow)
        ConsumeFlow(viewModel.finishPageFlow) { navigator.pop() }
    }
}
