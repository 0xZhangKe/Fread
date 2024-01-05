package com.zhangke.utopia.activitypub.app.internal.screen.add

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddInstanceViewModel @Inject constructor(
    private val instanceAdapter: ActivityPubInstanceAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddInstanceUiState(
            query = null,
            searching = false,
            errorMessage = null,
            inInstanceDetailPage = false,
            instance = null,
        )
    )
    val uiState: StateFlow<AddInstanceUiState> = _uiState

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

    }
}
