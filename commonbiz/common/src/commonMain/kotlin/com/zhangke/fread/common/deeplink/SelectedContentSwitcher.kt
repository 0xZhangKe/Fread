package com.zhangke.fread.common.deeplink

import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.FreadContent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class SelectedContentSwitcher @Inject constructor() {

    private val _selectedContentFlow = MutableSharedFlow<FreadContent>(1)
    val selectedContentFlow get() = _selectedContentFlow.asSharedFlow()

    suspend fun switchToContent(content: FreadContent) {
        _selectedContentFlow.emit(content)
    }

    fun resetReplayCache() {
        _selectedContentFlow.resetReplayCache()
    }
}
