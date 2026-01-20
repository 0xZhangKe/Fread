package com.zhangke.fread.common.deeplink

import com.zhangke.fread.status.model.FreadContent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SelectedContentSwitcher () {

    private val _selectedContentFlow = MutableSharedFlow<FreadContent>(1)
    val selectedContentFlow get() = _selectedContentFlow.asSharedFlow()

    suspend fun switchToContent(content: FreadContent) {
        _selectedContentFlow.emit(content)
    }

    fun resetReplayCache() {
        _selectedContentFlow.resetReplayCache()
    }
}