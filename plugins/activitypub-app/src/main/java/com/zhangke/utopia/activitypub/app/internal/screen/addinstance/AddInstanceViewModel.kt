package com.zhangke.utopia.activitypub.app.internal.screen.addinstance

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.platform.BlogPlatform
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddInstanceViewModel @Inject constructor(
    private val instanceAdapter: ActivityPubInstanceAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    private val accountManager: ActivityPubAccountManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddInstanceUiState(
            query = "m.cmx.im",//TODO change to empty string
            searching = false,
            errorMessage = null,
            inInstanceDetailPage = false,
            instance = null,
        )
    )
    val uiState: StateFlow<AddInstanceUiState> = _uiState

    private val _openLoginFlow = MutableSharedFlow<List<BlogPlatform>>()
    val openLoginFlow: SharedFlow<List<BlogPlatform>> get() = _openLoginFlow

    private val _contentConfigFlow = MutableSharedFlow<ContentConfig>()
    val contentConfigFlow: SharedFlow<ContentConfig> get() = _contentConfigFlow

    init {
        launchInViewModel {
            val initAccountList = accountManager.getAllLoggedAccount()
            accountManager.getAllAccountFlow()
                .collect { currentAccountList ->
                    if (initAccountList.isNotEmpty()) return@collect
                    if (currentAccountList.isEmpty()) return@collect
                    if (_uiState.value.instance == null) return@collect
                    emitCurrentContentConfigFlow()
                }
        }
    }

    fun onBackClick() {
        _uiState.value = _uiState.value.copy(
            inInstanceDetailPage = false,
        )
    }

    fun onQueryInput(query: String) {
        _uiState.value = _uiState.value.copy(
            query = query,
        )
    }

    fun onErrorMessageDismiss() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
        )
    }

    fun onSearchClick() {
        if (_uiState.value.searching) return
        val baseUrl = _uiState.value.query?.let(FormalBaseUrl::parse)
        if (baseUrl == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = textOf(R.string.add_instance_input_service_error),
            )
            return
        }
        launchInViewModel {
            _uiState.value = _uiState.value.copy(
                searching = true,
            )
            platformRepo.getInstanceEntity(baseUrl)
                .map(instanceAdapter::toInstance)
                .onSuccess { instance ->
                    _uiState.update {
                        it.copy(
                            searching = false,
                            instance = instance,
                            inInstanceDetailPage = true,
                        )
                    }
                }.onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            searching = false,
                            errorMessage = throwable.message?.let(::textOf),
                        )
                    }
                }
        }
    }

    fun onConfirmClick() {
        val instance = _uiState.value.instance ?: return
        launchInViewModel {
            if (accountManager.getAllLoggedAccount().isEmpty()) {
                _openLoginFlow.emit(listOf(instanceAdapter.toPlatform(instance)))
            } else {
                emitCurrentContentConfigFlow()
            }
        }
    }

    private suspend fun emitCurrentContentConfigFlow() {
        val instance = _uiState.value.instance ?: return
        val config = ContentConfig.ActivityPubContent(
            id = 0,
            name = instance.title,
            baseUrl = instance.baseUrl,
        )
        _contentConfigFlow.emit(config)
    }
}
