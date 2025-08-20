package com.zhangke.framework.composable.collapsable

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

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
        rememberSaveable(maxPx, minPx, saver = CollapsableTopBarLayoutConnection.Saver) {
            CollapsableTopBarLayoutConnection(maxPx, minPx)
        }.also {
            it.contentCanScrollBackward = contentCanScrollBackward
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
    private val maxPx: Float,
    private val minPx: Float,
) : NestedScrollConnection, ICollapsableTopBarLayoutConnection {

    var contentCanScrollBackward: State<Boolean>? = null

    private var topBarHeight: Float = maxPx
        set(value) {
            field = value
            progress = 1 - (topBarHeight - minPx) / (maxPx - minPx)
        }

    override var progress: Float by mutableFloatStateOf(0F)
        private set

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (available.y == 0f) return Velocity.Zero
        val startHeight = topBarHeight
        val targetHeight = exponentialDecay<Float>().calculateTargetValue(
            initialValue = startHeight,
            initialVelocity = available.y,
        ).coerceAtLeast(minPx).coerceAtMost(maxPx)
        if (topBarHeight == targetHeight) return available
        animate(
            initialValue = startHeight,
            targetValue = targetHeight
        ) { value, _ ->
            topBarHeight = value
        }
        val consumedY = targetHeight - startHeight
        return if (available.y > 0 && consumedY > 0 || available.y < 0 && consumedY < 0) {
            available
        } else {
            Velocity.Zero
        }
    }

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
        val height = topBarHeight
        if (height == minPx) {
            if (available.y > 0F) {
                return if (contentCanScrollBackward?.value == true) {
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

    companion object {

        val Saver: Saver<CollapsableTopBarLayoutConnection, *> = listSaver(
            save = {
                listOf(it.minPx, it.maxPx, it.topBarHeight)
            },
            restore = {
                CollapsableTopBarLayoutConnection(
                    minPx = it[0],
                    maxPx = it[1],
                ).apply {
                    topBarHeight = it[2]
                }
            }
        )
    }
}
