package com.zhangke.utopia.commonbiz.shared.screen.status.context

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.composable.updateToLoading
import com.zhangke.framework.composable.updateToSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StatusContextViewModel(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    lateinit var anchorStatus: Status

    private val _uiState = MutableStateFlow(LoadableState.idle<StatusContextUiState>())
    private val uiState: StateFlow<LoadableState<StatusContextUiState>> get() = _uiState

    fun onPrepares() {
        loadStatusContext()
    }

    private fun loadStatusContext() {
        if (_uiState.value.isLoading) return
        launchInViewModel {
            _uiState.updateToLoading()
            statusProvider.statusResolver
                .getStatusContext(anchorStatus)
                .onSuccess {
                    _uiState.updateToSuccess(it.toUiState())
                }.onFailure {
                    _uiState.updateToFailed(it)
                }
        }
    }

    private fun StatusContext.toUiState(): StatusContextUiState {
        return StatusContextUiState(
            anchorStatus = anchorStatus,
            ancestors = ancestors,
            descendants = descendants,
        )
    }
}
