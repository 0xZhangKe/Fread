package com.zhangke.framework.composable.inline

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.sensitive.SensitiveLazyColumn
import com.zhangke.framework.composable.sensitive.SensitiveLazyColumnState
import com.zhangke.framework.composable.sensitive.transform

@Composable
fun InlineVideoLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    indexMapping: ((Int) -> Int) = { it },
    content: LazyListScope.() -> Unit,
) {
    val sensitiveState = remember(state) {
        mutableStateOf(
            SensitiveLazyColumnState(
                firstVisibleIndex = 0,
                firstVisiblePercent = 0F,
                lastVisibleIndex = 0,
                lastVisiblePercent = 0F,
                isScrollInProgress = false,
            )
        )
    }
    val localSensitiveState by sensitiveState
    val playableIndexRecorder = remember(state) {
        PlayableIndexRecorder()
    }
    LaunchedEffect(localSensitiveState) {
        playableIndexRecorder.updateLayoutState(localSensitiveState)
    }
    CompositionLocalProvider(
        LocalPlayableIndexRecorder provides playableIndexRecorder
    ) {
        SensitiveLazyColumn(
            modifier = modifier,
            state = state,
            onSensitiveLayoutChanged = {
                sensitiveState.value = it.transform(indexMapping)
            },
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            flingBehavior = flingBehavior,
            userScrollEnabled = userScrollEnabled,
            content = content,
        )
    }
}
