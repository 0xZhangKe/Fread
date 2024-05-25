package com.zhangke.framework.composable

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

val LocalImmersiveViewModeManager = staticCompositionLocalOf {
    ImmersiveViewModeManager()
}

/**
 * It's not system immersive, just a post immersive view mode.
 */
class ImmersiveViewModeManager {

    private val inImmersiveFlow = MutableStateFlow(false)
    val inImmersiveMode = inImmersiveFlow.asStateFlow()

    fun openImmersiveMode() {
        inImmersiveFlow.value = true
    }

    fun closeImmersiveMode() {
        inImmersiveFlow.value = false
    }
}
