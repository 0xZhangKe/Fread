package com.zhangke.fread.status.ui.common

import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.fread.status.model.ContentConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
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
    val switchToNextTabFlow: SharedFlow<Unit> get() = _switchToNextTabFlow.asSharedFlow()

    private val _openDrawerFlow = MutableSharedFlow<Unit>()
    val openDrawerFlow: SharedFlow<Unit> get() = _openDrawerFlow.asSharedFlow()

    private val _inImmersiveFlow = MutableStateFlow(false)
    val inImmersiveFlow: StateFlow<Boolean> get() = _inImmersiveFlow.asStateFlow()

    private val _scrollToContentTabFlow = MutableSharedFlow<ContentConfig>()
    val scrollToContentTabFlow: SharedFlow<ContentConfig> get() = _scrollToContentTabFlow.asSharedFlow()

    private val _scrollToTopFlow = MutableSharedFlow<Unit>()
    val scrollToTopFlow: SharedFlow<Unit> get() = _scrollToTopFlow.asSharedFlow()

    private val _contentScrollInpProgress = MutableStateFlow(false)
    val contentScrollInpProgress: StateFlow<Boolean> get() = _contentScrollInpProgress.asStateFlow()

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
        }
    }

    fun closeImmersiveMode(coroutineScope: CoroutineScope) {
        toggleImmersiveJob?.cancel()
        toggleImmersiveJob = coroutineScope.launch {
            delay(IMMERSIVE_MODE_DELAY)
            _inImmersiveFlow.emit(false)
        }
    }

    fun updateContentScrollInProgress(scrollInProgress: Boolean) {
        Log.d("F_TEST", "updateContentScrollInProgress: $scrollInProgress")
        _contentScrollInpProgress.value = scrollInProgress
    }

    suspend fun scrollToContentTab(contentConfig: ContentConfig) {
        _scrollToContentTabFlow.emit(contentConfig)
    }

    suspend fun scrollToTop() {
        _scrollToTopFlow.emit(Unit)
    }
}
