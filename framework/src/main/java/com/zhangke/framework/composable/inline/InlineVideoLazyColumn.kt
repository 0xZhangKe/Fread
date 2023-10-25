package com.zhangke.framework.composable.inline

import android.util.Log
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.sensitive.SensitiveLazyColumnState

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
    content: LazyListScope.() -> Unit,
) {
    val sensitiveState = remember {
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
    val localState by sensitiveState
    var activeInlineIndex by remember {
        mutableIntStateOf(-1)
    }
    val playableIndexRecorder = remember {
        PlayableIndexRecorder()
    }
    LaunchedEffect(localState) {
        val playableIndexList = playableIndexRecorder.getIntervalIndexList(
            localState.firstVisibleIndex,
            localState.lastVisibleIndex,
        )

    }
    CompositionLocalProvider(
        LocalPlayableIndexRecorder provides playableIndexRecorder
    ) {
        LazyColumn(
            modifier = modifier,
            state = state,
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
