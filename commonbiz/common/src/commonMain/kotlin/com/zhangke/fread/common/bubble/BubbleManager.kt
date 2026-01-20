package com.zhangke.fread.common.bubble

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BubbleManager () {

    private val _bubbleListFlow = MutableStateFlow<List<Bubble>>(emptyList())
    val bubbleListFlow get() = _bubbleListFlow.asStateFlow()

    suspend fun addBubble(bubble: Bubble) {
        _bubbleListFlow.emit(_bubbleListFlow.value + bubble)
    }
}

val LocalBubbleManager = staticCompositionLocalOf<BubbleManager> {
    error("No BubbleManager provided")
}