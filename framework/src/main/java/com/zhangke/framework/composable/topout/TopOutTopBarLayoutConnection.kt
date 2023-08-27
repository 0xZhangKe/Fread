package com.zhangke.framework.composable.topout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

@Composable
fun rememberTopOutTopBarLayoutConnection(
    topBarHeight: Float,
): TopOutTopBarLayoutConnection {
    return remember(topBarHeight) {
        TopOutTopBarLayoutConnection(topBarHeight)
    }
}

class TopOutTopBarLayoutConnection(
    private val topBarHeight: Float,
) : NestedScrollConnection {

    var topMargin by mutableStateOf(topBarHeight)

    // content to screen margin
    private var _topMargin: Float = topBarHeight
        private set(value) {
            field = value
            topMargin = value
        }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val margin = _topMargin

        if (margin + available.y > topBarHeight) {
            _topMargin = topBarHeight
            return Offset(0f, topBarHeight - margin)
        }

        if (margin + available.y < 0F) {
            _topMargin = 0F
            return Offset(0f, -margin)
        }

        _topMargin += available.y

        return Offset(0f, available.y)
    }
}
