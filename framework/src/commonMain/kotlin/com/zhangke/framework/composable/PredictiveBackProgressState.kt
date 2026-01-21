package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState

@Stable
class PredictiveBackProgressState internal constructor(initial: Float) {
    var progress by mutableFloatStateOf(initial) // 0f..1f
}

@Composable
fun rememberPredictiveBackProgressState(): PredictiveBackProgressState {
    val state = rememberSaveable { PredictiveBackProgressState(0f) }
    val navEventState = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationEventHandler(
        state = navEventState,
        isBackEnabled = true,
        onBackCancelled = { state.progress = 0f },
        onBackCompleted = { state.progress = 0f },
    )
    return state
}
