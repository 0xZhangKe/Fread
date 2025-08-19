package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

@Composable
fun rememberCollapsableTopBarScrollConnection(
    maxPx: Float,
    minPx: Float,
): CollapsableTopBarScrollConnection {
    return rememberSaveable(minPx, maxPx, saver = CollapsableTopBarScrollConnection.Saver) {
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

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return handleScroll(available)
    }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        return handleScroll(available)
    }

    private fun handleScroll(available: Offset): Offset {
        if (available.y <= 0 && topBarHeight <= minPx) return Offset.Zero
        if (available.y >= 0 && topBarHeight >= maxPx) return Offset.Zero
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

    companion object {

        val Saver: Saver<CollapsableTopBarScrollConnection, *> = listSaver(
            save = {
                listOf(it.maxPx, it.minPx, it.topBarHeight)
            },
            restore = {
                CollapsableTopBarScrollConnection(
                    maxPx = it[0],
                    minPx = it[1],
                ).apply {
                    topBarHeight = it[2]
                }
            },
        )
    }
}
