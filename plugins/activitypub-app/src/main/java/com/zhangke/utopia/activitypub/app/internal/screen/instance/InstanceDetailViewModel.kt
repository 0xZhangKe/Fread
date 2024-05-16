package com.zhangke.utopia.activitypub.app.internal.screen.instance

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InstanceDetailViewModel @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
) : ViewModel() {

    lateinit var serverBaseUrl: FormalBaseUrl

    private val _uiState = MutableStateFlow(
        InstanceDetailUiState(
            loading = false,
            baseUrl = null,
            instance = null,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _contentConfigFlow = MutableSharedFlow<Unit>()
    val contentConfigFlow: SharedFlow<Unit> get() = _contentConfigFlow

    fun onPrepared() {
        _uiState.value = _uiState.value.copy(
            loading = true,
            baseUrl = serverBaseUrl
        )
        launchInViewModel {
            val platform = platformRepo.getInstanceEntity(serverBaseUrl).getOrNull()
            _uiState.value = _uiState.value.copy(
                loading = false,
                instance = platform,
            )
        }
    }

    private suspend fun emitCurrentContentConfigFlow() {
        _contentConfigFlow.emit(Unit)
    }
}
