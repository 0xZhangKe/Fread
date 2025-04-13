package com.zhangke.fread.activitypub.app.internal.screen.list.edit

import androidx.lifecycle.ViewModel
import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class EditListViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    @Assisted private val role: IdentityRole,
    @Assisted private val serializedList: String,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
            serializedList: String,
        ): EditListViewModel
    }

    private val entity: ActivityPubListEntity = globalJson.decodeFromString(serializedList)

    private val _uiState = MutableStateFlow(EditListUiState.default(entity))
    val uiState = _uiState.asStateFlow()

    private val _snackBarFlow = MutableSharedFlow<TextString>()
    val snackBarFlow = _snackBarFlow.asSharedFlow()

    init {
        launchInViewModel { getListDetail() }
    }

    private suspend fun getListDetail() {
        getAccountList()
    }

    fun onRetryLoadAccountsClick() {
        launchInViewModel { getAccountList() }
    }

    fun onPolicySelect(policy: ListRepliesPolicy){

    }

    private suspend fun getAccountList() {
        _uiState.update { it.copy(accountsLoading = true) }
        clientManager.getClient(role)
            .accountRepo
            .getAccountsInList(entity.id)
            .onSuccess { list ->
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
