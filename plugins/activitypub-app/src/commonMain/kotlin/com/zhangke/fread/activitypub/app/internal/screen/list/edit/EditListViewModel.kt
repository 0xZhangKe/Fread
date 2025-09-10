package com.zhangke.fread.activitypub.app.internal.screen.list.edit

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitInViewModel
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class EditListViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    @Assisted private val locator: PlatformLocator,
    @Assisted private val serializedList: String,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            locator: PlatformLocator,
            serializedList: String,
        ): EditListViewModel
    }

    private val entity: ActivityPubListEntity = globalJson.decodeFromString(serializedList)

    private val _uiState = MutableStateFlow(EditListUiState.default(entity))
    val uiState = _uiState.asStateFlow()

    private val _snackBarFlow = MutableSharedFlow<TextString>()
    val snackBarFlow = _snackBarFlow.asSharedFlow()

    private val _finishPageFlow = MutableSharedFlow<Unit>()
    val finishPageFlow = _finishPageFlow.asSharedFlow()

    private var originalAccountList: List<ActivityPubAccountEntity> = emptyList()

    init {
        launchInViewModel { getListDetail() }
    }

    private suspend fun getListDetail() {
        getAccountList()
    }

    fun onRetryLoadAccountsClick() {
        launchInViewModel { getAccountList() }
    }

    fun onPolicySelect(policy: ListRepliesPolicy) {
        _uiState.update { it.copy(repliesPolicy = policy) }
        checkContentHasChanged()
    }

    fun onDeleteClick() {
        launchInViewModel {
            clientManager.getClient(locator)
                .listsRepo
                .deleteList(entity.id)
                .onSuccess {
                    _finishPageFlow.emit(Unit)
                }.onFailure { t ->
                    _snackBarFlow.emitTextMessageFromThrowable(t)
                }
        }
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

    fun onRemoveAccount(accountEntity: ActivityPubAccountEntity) {
        if (!originalAccountList.any { it.id == accountEntity.id }) {
            _uiState.update { it.copy(accountList = it.accountList - accountEntity) }
            return
        }
        _uiState.update { it.copy(showLoadingCover = true) }
        launchInViewModel {
            clientManager.getClient(locator)
                .listsRepo
                .deleteAccountsInList(
                    listId = entity.id,
                    accounts = listOf(accountEntity.id),
                ).onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            showLoadingCover = false,
                            accountList = state.accountList - accountEntity,
                        )
                    }
                    originalAccountList = originalAccountList - accountEntity
                }.onFailure { t ->
                    _uiState.update { state ->
                        state.copy(showLoadingCover = false)
                    }
                    _snackBarFlow.emitTextMessageFromThrowable(t)
                }
        }
    }

    fun onSaveClick() {
        if (_uiState.value.name.text.isEmpty()) {
            _snackBarFlow.emitInViewModel(textOf(LocalizedString.activity_pub_add_list_name_is_empty))
            return
        }
        _uiState.update { it.copy(showLoadingCover = true) }
        viewModelScope.launch {
            supervisorScope {
                val settingDeferred = async { updateSettingPart() }
                val accountDeferred = async { updateAccountList() }
                val settingResult = settingDeferred.await()
                val accountResult = accountDeferred.await()
                _uiState.update { it.copy(showLoadingCover = false) }
                if (settingResult.isFailure) {
                    _snackBarFlow.emitTextMessageFromThrowable(settingResult.exceptionOrThrow())
                } else if (accountResult.isFailure) {
                    _snackBarFlow.emitTextMessageFromThrowable(accountResult.exceptionOrThrow())
                } else {
                    _finishPageFlow.emit(Unit)
                }
            }
        }
    }

    fun onAddUser(user: ActivityPubAccountEntity) {
        if (_uiState.value.accountList.any { it.id == entity.id }) return
        _uiState.update { it.copy(accountList = it.accountList + user) }
    }

    private suspend fun updateSettingPart(): Result<Unit> {
        if (!checkSettingHasChanged()) return Result.success(Unit)
        return clientManager.getClient(locator).listsRepo
            .updateList(
                listId = entity.id,
                title = uiState.value.name.text,
                repliesPolicy = uiState.value.repliesPolicy.apiName,
                exclusive = uiState.value.exclusive,
            ).map { }
    }

    private suspend fun updateAccountList(): Result<Unit> {
        if (!checkAccountHasChanged()) return Result.success(Unit)
        val ids = originalAccountList.map { it.id }.toSet()
        val newAccounts = _uiState.value.accountList.filter { !ids.contains(it.id) }
        return clientManager.getClient(locator).listsRepo
            .postAccountInList(
                listId = entity.id,
                accountIds = newAccounts.map { it.id },
            ).map { }
    }

    private fun checkContentHasChanged() {
        val hasChanged = checkSettingHasChanged() || checkAccountHasChanged()
        if (_uiState.value.contentHasChanged != hasChanged) {
            _uiState.update { it.copy(contentHasChanged = hasChanged) }
        }
    }

    private fun checkSettingHasChanged(): Boolean {
        if (entity.title != uiState.value.name.text) {
            return true
        }
        if (entity.repliesPolicy != uiState.value.repliesPolicy.apiName) {
            return true
        }
        if (entity.exclusive != uiState.value.exclusive) {
            return true
        }
        return false
    }

    private fun checkAccountHasChanged(): Boolean {
        if (originalAccountList.size != _uiState.value.accountList.size) {
            return true
        }
        val ids = originalAccountList.map { it.id }.toSet()
        for (entity in _uiState.value.accountList) {
            if (!ids.contains(entity.id)) return true
        }
        return false
    }

    private suspend fun getAccountList() {
        _uiState.update { it.copy(accountsLoading = true) }
        clientManager.getClient(locator)
            .listsRepo
            .getAccountsInList(entity.id)
            .onSuccess { list ->
                originalAccountList = list
                _uiState.update { state ->
                    state.copy(
                        accountsLoading = false,
                        accountList = list,
                    )
                }
            }.onFailure { t ->
                _uiState.update { state ->
                    state.copy(
                        accountsLoading = false,
                        loadAccountsError = t,
                    )
                }
            }
    }
}
