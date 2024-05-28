package com.zhangke.utopia.status.ui.common

import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.utopia.status.model.ContentConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

val LocalMainTabConnection = staticCompositionLocalOf {
    MainTabConnection()
}

class MainTabConnection {

    companion object {


        private val IMMERSIVE_MODE_DELAY = 500.milliseconds
    }

    private val _switchToNextTabFlow = MutableSharedFlow<Unit>()
    val switchToNextTabFlow = _switchToNextTabFlow.asSharedFlow()

    private val _openDrawerFlow = MutableSharedFlow<Unit>()
    val openDrawerFlow = _openDrawerFlow.asSharedFlow()

    private val _inImmersiveFlow = MutableStateFlow(false)
    val inImmersiveFlow = _inImmersiveFlow.asStateFlow()

    private val _scrollToContentTabFlow = MutableSharedFlow<ContentConfig>()
    val scrollToContentTabFlow = _scrollToContentTabFlow.asSharedFlow()

    private var toggleImmersiveJob: Job? = null

    suspend fun switchToNextTab() {
        _switchToNextTabFlow.emit(Unit)
    }

    suspend fun openDrawer() {
        _openDrawerFlow.emit(Unit)
    }

    fun openImmersiveMode(coroutineScope: CoroutineScope) {
        toggleImmersiveJob?.cancel()
        toggleImmersiveJob = coroutineScope.launch {
            delay(IMMERSIVE_MODE_DELAY)
            _inImmersiveFlow.emit(true)
            Log.d("U_TEST", "openImmersiveMode")
        }
    }

    fun closeImmersiveMode(coroutineScope: CoroutineScope) {
        toggleImmersiveJob?.cancel()
        toggleImmersiveJob = coroutineScope.launch {
            delay(IMMERSIVE_MODE_DELAY)
            _inImmersiveFlow.emit(false)
            Log.d("U_TEST", "closeImmersiveMode")
        }
    }

    suspend fun scrollToContentTab(contentConfig: ContentConfig) {
        _scrollToContentTabFlow.emit(contentConfig)
    }
}
