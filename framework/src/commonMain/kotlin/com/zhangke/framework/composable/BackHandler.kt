package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState

@Composable
fun BackHandler(enabled: Boolean, block: () -> Unit) {
    val navState = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationBackHandler(
        state = navState,
        isBackEnabled = enabled,
        onBackCancelled = {},
        onBackCompleted = block,
    )
}
