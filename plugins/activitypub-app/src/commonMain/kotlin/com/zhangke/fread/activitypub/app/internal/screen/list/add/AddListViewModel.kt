package com.zhangke.fread.activitypub.app.internal.screen.list.add

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitInViewModel
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.screen.list.edit.ListRepliesPolicy
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class AddListViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    @Assisted private val locator: PlatformLocator,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(locator: PlatformLocator): AddListViewModel
    }

    private val _uiState = MutableStateFlow(AddListUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarFlow = MutableSharedFlow<TextString>()
    val snackBarFlow = _snackBarFlow.asSharedFlow()

    private val _finishPageFlow = MutableSharedFlow<Unit>()
    val finishPageFlow = _finishPageFlow.asSharedFlow()

    fun onPolicySelect(policy: ListRepliesPolicy) {
        _uiState.update { it.copy(repliesPolicy = policy) }
        checkContentHasChanged()
    }

    fun onNameChangeRequest(name: TextFieldValue) {
        _uiState.update { state ->
            state.copy(name = name)
        }
        checkContentHasChanged()
    }

    fun onExclusiveChanged(exclusive: Boolean) {
        _uiState.update { state ->
            state.copy(exclusive = exclusive)
        }
        checkContentHasChanged()
    }

    fun onAddAccount(entity: ActivityPubAccountEntity) {
        if (_uiState.value.accountList.any { it.id == entity.id }) return
        _uiState.update { state ->
            state.copy(accountList = state.accountList + entity)
        }
        checkContentHasChanged()
    }

    fun onRemoveAccount(entity: ActivityPubAccountEntity) {
        _uiState.update { state ->
            state.copy(accountList = state.accountList - entity)
        }
        checkContentHasChanged()
    }

    fun onSaveClick() {
        val currentUiState = _uiState.value
        if (!currentUiState.contentHasChanged) return
        if (currentUiState.name.text.isEmpty()) {
            _snackBarFlow.emitInViewModel(textOf(LocalizedString.activity_pub_add_list_name_is_empty))
            return
        }
        launchInViewModel {
            _uiState.update { it.copy(showLoadingCover = true) }
            val listsRepo = clientManager.getClient(locator).listsRepo
            listsRepo.createList(
                title = currentUiState.name.text,
                repliesPolicy = currentUiState.repliesPolicy.apiName,
                exclusive = currentUiState.exclusive,
            ).onSuccess { entity ->
                if (currentUiState.accountList.isNotEmpty()) {
                    listsRepo.postAccountInList(
                        listId = entity.id,
                        accountIds = currentUiState.accountList.map { it.id },
                    ).onSuccess {
                        _uiState.update { it.copy(showLoadingCover = false) }
                        _finishPageFlow.emit(Unit)
                    }.onFailure {
                        _uiState.update { it.copy(showLoadingCover = false) }
                        _snackBarFlow.emitTextMessageFromThrowable(it)
                    }
                } else {
                    _uiState.update { it.copy(showLoadingCover = false) }
                    _finishPageFlow.emit(Unit)
                }
            }.onFailure {
                _uiState.update { it.copy(showLoadingCover = false) }
                _snackBarFlow.emitTextMessageFromThrowable(it)
            }
        }
    }

    private fun checkContentHasChanged() {
        var hasChanged = false
        val currentUiState = _uiState.value
        if (currentUiState.name.text.isNotEmpty()) {
            hasChanged = true
        }
        if (currentUiState.repliesPolicy != ListRepliesPolicy.LIST) {
            hasChanged = true
        }
        if (currentUiState.exclusive) {
            hasChanged = true
        }
        if (currentUiState.accountList.isNotEmpty()) {
            hasChanged = true
        }
        if (hasChanged != currentUiState.contentHasChanged) {
            _uiState.update { it.copy(contentHasChanged = hasChanged) }
        }
    }
}
