package com.zhangke.fread.common.status

import com.zhangke.fread.status.model.StatusUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class StatusUpdater () {

    private val _statusUpdateFlow = MutableSharedFlow<StatusUiState>()
    val statusUpdateFlow: SharedFlow<StatusUiState> get() = _statusUpdateFlow.asSharedFlow()

    suspend fun update(status: StatusUiState) {
        _statusUpdateFlow.emit(status)
    }
}