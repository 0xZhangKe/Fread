package com.zhangke.framework.composable.photo

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.toSize
import com.zhangke.framework.composable.transformable.rememberTransformableCleverlyState
import com.zhangke.framework.composable.transformable.transformableCleverly
import kotlinx.coroutines.launch

@ExperimentalPhotoApi
@Composable
fun PhotoBox(
    modifier: Modifier = Modifier,
    state: PhotoState = rememberPhotoState(),
    enabled: Boolean = true,
    contentAlignment: Alignment = Alignment.Center,
    propagateMinConstraints: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val transformableState = rememberTransformableCleverlyState { zoomChange, panChange ->
        if (enabled) {
            state.currentScale *= zoomChange
            state.currentOffset += panChange
        }
    }
    Box(
        modifier = modifier
            .onSizeChanged { state.layoutSize = it.toSize() }
            .pointerInputs(
                enabled = enabled && state.isScaled,
                onDrag = { dragAmount ->
                    state.currentOffset += dragAmount
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        state.performFling(Offset(velocity.x, velocity.y))
                    }
                },
            )
            .pointerInputs(
                enabled = enabled,
                onDoubleTap = {
                    if (state.isScaled) {
                        coroutineScope.launch {
                            state.animateToInitialState()
                        }
                    } else {
                        coroutineScope.launch {
                            state.animateScale(state.maximumScale)
                        }
                    }
                }
            )
            .clipToBounds()
            .graphicsLayer {
                scaleX = state.currentScale
                scaleY = state.currentScale
                translationX = state.currentOffset.x
                translationY = state.currentOffset.y
            }
            .transformableCleverly(transformableState),
        contentAlignment = contentAlignment,
        propagateMinConstraints = propagateMinConstraints,
        content = content,
    )
}

private fun Modifier.pointerInputs(
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

private fun Modifier.pointerInputs(
    enabled: Boolean,
    onDoubleTap: (position: Offset) -> Unit,
): Modifier {
    if (enabled.not()) {
        return this
    }
    return pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = onDoubleTap,
        )
    }
}
