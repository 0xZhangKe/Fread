package com.zhangke.fread.common.action

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

val LocalComposableActions = staticCompositionLocalOf { ComposableActions }

object ComposableActions {

    private val _actionFlow = MutableSharedFlow<String>(replay = 1)
    val actionFlow: SharedFlow<String> get() = _actionFlow

    suspend fun post(uri: String) {
        _actionFlow.emit(uri)
    }

    fun resetReplayCache() {
        _actionFlow.resetReplayCache()
    }
}
