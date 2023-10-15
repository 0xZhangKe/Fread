package com.zhangke.framework.composable.image.viewer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.toSize
import com.zhangke.framework.composable.transformable.rememberTransformableCleverlyState
import com.zhangke.framework.composable.transformable.transformableCleverly
import com.zhangke.framework.ktx.isSingle
import com.zhangke.framework.utils.pxToDp
import kotlinx.coroutines.launch

private val infinityConstraints = Constraints()

@Composable
fun ImageViewer(
    state: ImageViewerState,
    modifier: Modifier = Modifier,
    onStartDismiss: () -> Unit,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    var latestSize: Size? by remember {
        mutableStateOf(null)
    }
    var latestZoomChang: Float by remember {
        mutableFloatStateOf(1F)
    }
    val transformableState = rememberTransformableCleverlyState { zoomChange, _ ->
        if (latestZoomChang != zoomChange) {
            state.zoom(zoomChange)
        }
        latestZoomChang = zoomChange
    }
    BackHandler {
        coroutineScope.launch {
            state.startDismiss()
        }
    }
    state.onDismissRequest = onDismissRequest
    state.onStartDismiss = onStartDismiss
    Layout(
        modifier = modifier
            .onGloballyPositioned { position ->
                val currentSize = position.size.toSize()
                if (currentSize != latestSize) {
                    coroutineScope.launch {
                        state.updateLayoutSize(currentSize)
                    }
                    latestSize = currentSize
                }
            }
            .pointerInput(state) {
                detectTapGestures(
                    onDoubleTap = {
                        if (state.exceed) {
                            coroutineScope.launch {
                                state.animateToStandard()
                            }
                        } else {
                            coroutineScope.launch {
                                state.animateToBig()
                            }
                        }
                    }
                )
            }
            .draggableInfinity(
                enabled = true,
                onDrag = { offset ->
                    state.drag(offset)
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        state.dragStop(velocity)
                    }
                }
            )
            .transformableCleverly(transformableState),
        content = {
            Box(
                modifier = Modifier
                    .offset(
                        x = state.currentOffsetXPixel.pxToDp(density),
                        y = state.currentOffsetYPixel.pxToDp(density)
                    )
                    .width(state.currentWidthPixel.pxToDp(density))
                    .height(state.currentHeightPixel.pxToDp(density))
            ) {
                content()
            }
        }
    ) { measurables, constraints ->
        if (measurables.isSingle().not()) {
            throw IllegalStateException("InfiniteBox is only allowed to have one children!")
        }
        val placeable = measurables.first().measure(infinityConstraints)

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.placeRelative(0, 0)
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
