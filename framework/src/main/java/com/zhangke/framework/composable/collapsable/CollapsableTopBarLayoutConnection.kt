package com.zhangke.framework.composable.collapsable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

@Composable
fun rememberCollapsableTopBarLayoutConnection(
    contentCanScrollBackward: State<Boolean>,
    maxPx: Float,
    minPx: Float,
): CollapsableTopBarLayoutConnection {
    return remember(contentCanScrollBackward, maxPx, minPx) {
        CollapsableTopBarLayoutConnection(contentCanScrollBackward, maxPx, minPx)
    }
}

class CollapsableTopBarLayoutConnection(
    private val contentCanScrollBackward: State<Boolean>,
    private val maxPx: Float,
    private val minPx: Float,
) : NestedScrollConnection {

    private var toolbarHeight: Float = maxPx
        set(value) {
            field = value
            progress = 1 - (toolbarHeight - minPx) / (maxPx - minPx)
        }

    var progress: Float by mutableStateOf(0F)
        private set

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val height = toolbarHeight

        if (height == minPx) {
            if (available.y > 0F) {
                return if (contentCanScrollBackward.value) {
                    Offset.Zero
                } else {
                    toolbarHeight += available.y
                    Offset(0F, available.y)
                }
            }
        }

        if (height + available.y > maxPx) {
            toolbarHeight = maxPx
            return Offset(0f, maxPx - height)
        }

        if (height + available.y < minPx) {
            toolbarHeight = minPx
            return Offset(0f, minPx - height)
        }

        toolbarHeight += available.y

        return Offset(0f, available.y)
    }
}
