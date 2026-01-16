package com.zhangke.fread.activitypub.app.internal.screen.list.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.activitypub.app.internal.screen.list.ListDetailPageContent
import com.zhangke.fread.activitypub.app.internal.screen.user.search.SearchUserScreenNavKey
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class EditListScreenNavKey(
    val locator: PlatformLocator,
    val serializedList: String,
) : NavKey

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditListScreen(
    locator: PlatformLocator,
    viewModel: EditListViewModel,
) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    val snackBarState = rememberSnackbarHostState()
    var showBackReminder by remember { mutableStateOf(false) }
    fun onBack() {
        if (uiState.contentHasChanged) {
            showBackReminder = true
        } else {
            backStack.removeLastOrNull()
        }
    }
    BackHandler(true) { onBack() }
    if (showBackReminder) {
        FreadDialog(
            onDismissRequest = { showBackReminder = false },
            contentText = stringResource(LocalizedString.activity_pub_add_list_back_reminder),
            onNegativeClick = { showBackReminder = false },
            onPositiveClick = {
                showBackReminder = false
                backStack.removeLastOrNull()
            },
        )
    }
    ConsumeFlow(SearchUserScreenNavKey.accountSelectedFlow.flow) {
        viewModel.onAddUser(it)
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
            val searchUserScreen = SearchUserScreenNavKey(locator, true)
            backStack.add(searchUserScreen)
        },
        onSaveClick = viewModel::onSaveClick,
        onBackClick = { onBack() },
        onRetryLoadAccountsClick = viewModel::onRetryLoadAccountsClick,
        onPolicySelect = viewModel::onPolicySelect,
        onDeleteClick = viewModel::onDeleteClick,
    )
    ConsumeSnackbarFlow(snackBarState, viewModel.snackBarFlow)
    ConsumeFlow(viewModel.finishPageFlow) { backStack.removeLastOrNull() }
}
