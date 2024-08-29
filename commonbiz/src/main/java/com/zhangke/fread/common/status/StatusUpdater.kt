package com.zhangke.fread.common.status

import com.zhangke.fread.common.status.model.StatusUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatusUpdater @Inject constructor() {

    private val _statusUpdateFlow = MutableSharedFlow<StatusUiState>()
    val statusUpdateFlow: SharedFlow<StatusUiState> get() = _statusUpdateFlow.asSharedFlow()

    suspend fun update(status: StatusUiState) {
        _statusUpdateFlow.emit(status)
    }
}
