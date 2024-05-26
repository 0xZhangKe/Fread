package com.zhangke.utopia.status.ui.common

import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.utopia.status.model.ContentConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

val LocalMainTabConnection = staticCompositionLocalOf {
    MainTabConnection()
}

class MainTabConnection {

    private val _switchToNextTabFlow = MutableSharedFlow<Unit>()
    val switchToNextTabFlow = _switchToNextTabFlow.asSharedFlow()

    private val _openDrawerFlow = MutableSharedFlow<Unit>()
    val openDrawerFlow = _openDrawerFlow.asSharedFlow()

    private val _inImmersiveFlow = MutableStateFlow(false)
    val inImmersiveFlow = _inImmersiveFlow.asStateFlow()

    private val _scrollToContentTabFlow = MutableSharedFlow<ContentConfig>()
    val scrollToContentTabFlow = _scrollToContentTabFlow.asSharedFlow()

    suspend fun switchToNextTab() {
        _switchToNextTabFlow.emit(Unit)
    }

    suspend fun openDrawer() {
        _openDrawerFlow.emit(Unit)
    }

    suspend fun openImmersiveMode() {
        _inImmersiveFlow.emit(true)
    }

    suspend fun closeImmersiveMode() {
        _inImmersiveFlow.emit(false)
    }

    suspend fun scrollToContentTab(contentConfig: ContentConfig) {
        _scrollToContentTabFlow.emit(contentConfig)
    }
}
