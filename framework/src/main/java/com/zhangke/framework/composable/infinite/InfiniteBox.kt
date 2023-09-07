package com.zhangke.framework.composable.infinite

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.toSize
import com.zhangke.framework.composable.Bounds
import com.zhangke.framework.ktx.isSingle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val infinityConstraints = Constraints()

@Composable
fun InfiniteBox(
    modifier: Modifier = Modifier,
    state: InfinityBoxState = rememberInfinityBoxState(),
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var layoutSize by remember {
        mutableStateOf(Size.Zero)
    }
    Layout(
        modifier = modifier
            .onSizeChanged {
                layoutSize = it.toSize()
                state.layoutSize = it.toSize()
                Log.d(
                    "U_TEST",
                    "InfiniteBox layoutSize ${it.toSize()}",
                )
            }
            .draggableInfinity(
                enabled = state.exceed,
                onDrag = { dragAmount ->
                    state.drag(dragAmount)
                },
                onDragStopped = { initialVelocity ->
                    coroutineScope.launch {
                        state.fling(initialVelocity)
                    }
                }
            ),
        content = content,
    ) { measurables, constraints ->
        if (measurables.isSingle().not()) {
            throw IllegalStateException("InfiniteBox is only allowed to have one children!")
        }
        val placeable = measurables.first().measure(infinityConstraints)
        state.exceed =
            placeable.width > constraints.maxWidth || placeable.height > constraints.maxHeight
        state.draggableBounds = Bounds(
            left = (-(placeable.width - layoutSize.width)).coerceAtMost(0F),
            top = (-(placeable.height - layoutSize.height)).coerceAtMost(0F),
            right = 0F,
            bottom = 0F,
        )
        Log.d(
            "U_TEST",
            "placeable size: ${placeable.width} * ${placeable.height}, bounds left:${state.draggableBounds.left}, top:${state.draggableBounds.top}",
        )
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.placeRelative(
                x = state.currentOffset.x.roundToInt(),
                y = state.currentOffset.y.roundToInt(),
            )
        }
    }
}

private fun Modifier.draggableInfinity(
    enabled: Boolean,
    onDrag: (dragAmount: Offset) -> Unit,
    onDragStopped: (velocity: Velocity) -> Unit,
): Modifier {
    val velocityTracker = VelocityTracker()
    return pointerInput(enabled) {
        if (enabled) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    velocityTracker.addPointerInputChange(change)
                    onDrag(dragAmount)
                },
                onDragEnd = {
                    val velocity = velocityTracker.calculateVelocity()
                    onDragStopped(velocity)
                },
                onDragCancel = {
                    val velocity = velocityTracker.calculateVelocity()
                    onDragStopped(velocity)
                },
            )
        }
    }
}
