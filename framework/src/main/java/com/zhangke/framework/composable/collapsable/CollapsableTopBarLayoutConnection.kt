package com.zhangke.framework.composable.collapsable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
): ICollapsableTopBarLayoutConnection {
    return if (maxPx <= 0F) {
        remember {
            StaticTopBarLayoutConnection()
        }
    } else {
        remember(contentCanScrollBackward, maxPx, minPx) {
            CollapsableTopBarLayoutConnection(contentCanScrollBackward, maxPx, minPx)
        }
    }
}

interface ICollapsableTopBarLayoutConnection : NestedScrollConnection {

    val progress: Float
}

class StaticTopBarLayoutConnection : NestedScrollConnection, ICollapsableTopBarLayoutConnection {

    override val progress: Float = 0F
}

class CollapsableTopBarLayoutConnection(
    private val contentCanScrollBackward: State<Boolean>,
    private val maxPx: Float,
    private val minPx: Float,
) : NestedScrollConnection, ICollapsableTopBarLayoutConnection {

    private var topBarHeight: Float = maxPx
        set(value) {
            field = value
            progress = 1 - (topBarHeight - minPx) / (maxPx - minPx)
        }

    override var progress: Float by mutableFloatStateOf(0F)
        private set

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val height = topBarHeight

        if (height == minPx) {
            if (available.y > 0F) {
                return if (contentCanScrollBackward.value) {
                    Offset.Zero
                } else {
                    topBarHeight += available.y
                    Offset(0F, available.y)
                }
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
