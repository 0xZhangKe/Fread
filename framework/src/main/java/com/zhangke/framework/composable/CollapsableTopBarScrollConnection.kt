package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

@Composable
fun rememberCollapsableTopBarScrollConnection(
    maxPx: Float,
    minPx: Float,
): CollapsableTopBarScrollConnection {
    return remember(minPx, maxPx) {
        CollapsableTopBarScrollConnection(maxPx, minPx)
    }
}

class CollapsableTopBarScrollConnection(
    private val maxPx: Float,
    private val minPx: Float,
) : NestedScrollConnection {

    private var topBarHeight: Float = maxPx
        set(value) {
            field = value
            progress = 1 - (topBarHeight - minPx) / (maxPx - minPx)
        }

    var progress: Float by mutableFloatStateOf(0F)
        private set

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val height = topBarHeight

        if (height == minPx) {
            if (available.y > 0F) {
                topBarHeight += available.y
                return Offset(0F, available.y)
            }
        }

        if (height + available.y > maxPx) {
            topBarHeight = maxPx
            return Offset(0f, maxPx - height)
        }

        if (height + available.y < minPx) {
            topBarHeight = minPx
            return Offset(0f, minPx - height)
        }

        topBarHeight += available.y

        return Offset(0f, available.y)
    }
}
