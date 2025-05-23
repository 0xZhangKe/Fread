package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.fread.status.model.FreadContent
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

val LocalNestedTabConnection = staticCompositionLocalOf {
    NestedTabConnection()
}

class NestedTabConnection {

    companion object {

        private val IMMERSIVE_MODE_DELAY = 500.milliseconds
    }

    private val _switchToNextTabFlow = MutableSharedFlow<Unit>()
    val switchToNextTabFlow: SharedFlow<Unit> get() = _switchToNextTabFlow.asSharedFlow()

    private val _openDrawerFlow = MutableSharedFlow<Unit>()
    val openDrawerFlow: SharedFlow<Unit> get() = _openDrawerFlow.asSharedFlow()

    private val _inImmersiveFlow = MutableStateFlow(false)
    val inImmersiveFlow: StateFlow<Boolean> get() = _inImmersiveFlow.asStateFlow()

    private val _scrollToContentTabFlow = MutableSharedFlow<FreadContent>()
    val scrollToContentTabFlow: SharedFlow<FreadContent> get() = _scrollToContentTabFlow.asSharedFlow()

    private val _scrollToTopFlow = MutableSharedFlow<Unit>()
    val scrollToTopFlow: SharedFlow<Unit> get() = _scrollToTopFlow.asSharedFlow()

    private val _contentScrollInpProgress = MutableStateFlow(false)
    val contentScrollInpProgress: StateFlow<Boolean> get() = _contentScrollInpProgress.asStateFlow()

    private val _refreshFlow = MutableSharedFlow<Unit>()
    val refreshFlow: SharedFlow<Unit> get() = _refreshFlow.asSharedFlow()

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
        _contentScrollInpProgress.value = scrollInProgress
    }

    suspend fun scrollToContentTab(contentConfig: FreadContent) {
        _scrollToContentTabFlow.emit(contentConfig)
    }

    suspend fun scrollToTop() {
        _scrollToTopFlow.emit(Unit)
    }

    suspend fun refresh() {
        _refreshFlow.emit(Unit)
    }
}

@Composable
fun ObserveScrollInProgressForConnection(lazyListState: LazyListState) {
    val nestedTabConnection = LocalNestedTabConnection.current
    LaunchedEffect(lazyListState.isScrollInProgress) {
        nestedTabConnection.updateContentScrollInProgress(lazyListState.isScrollInProgress)
    }
}
