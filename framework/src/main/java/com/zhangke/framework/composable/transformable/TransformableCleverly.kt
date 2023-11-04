package com.zhangke.framework.composable.transformable

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastAny
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlin.math.abs

fun Modifier.transformableCleverly(
    state: TransformableCleverlyState,
    zoomEnabled: Boolean = true,
    panEnabled: Boolean = true,
): Modifier = composed{

    val channel = remember { Channel<TransformEvent>(capacity = Channel.UNLIMITED) }
    if (zoomEnabled && panEnabled) {
        LaunchedEffect(state) {
            while (isActive) {
                var event = channel.receive()
                if (event !is TransformEvent.TransformStarted) continue
                try {
                    state.transform(MutatePriority.UserInput) {
                        while (event !is TransformEvent.TransformStopped) {
                            (event as? TransformEvent.TransformDelta)?.let {
                                transformBy(it.zoomChange, it.panChange)
                            }
                            event = channel.receive()
                        }
                    }
                } catch (_: CancellationException) {
                    // ignore the cancellation and start over again.
                }
            }
        }
    }
    val block: suspend PointerInputScope.() -> Unit = remember {
        {
            coroutineScope {
                awaitEachGesture {
                    try {
                        detectZoom(channel)
                    } catch (exception: CancellationException) {
                        if (!isActive) throw exception
                    } finally {
                        channel.trySend(TransformEvent.TransformStopped)
                    }
                }
            }
        }
    }
    if (zoomEnabled && panEnabled) Modifier.pointerInput(Unit, block) else Modifier
}

private sealed class TransformEvent {
    data object TransformStarted : TransformEvent()
    data object TransformStopped : TransformEvent()
    class TransformDelta(
        val zoomChange: Float,
        val panChange: Offset,
    ) : TransformEvent()
}

private suspend fun AwaitPointerEventScope.detectZoom(
    channel: Channel<TransformEvent>
) {
    var zoom = 1f
    var pan = Offset.Zero
    var pastTouchSlop = false
    val touchSlop = viewConfiguration.touchSlop
    awaitFirstDown(requireUnconsumed = false)
    do {
        val event = awaitPointerEvent()
        val canceled = event.changes.fastAny { it.isConsumed }
        if (!canceled) {
            val zoomChange = event.calculateZoom()
            val panChange = event.calculatePan()

            if (!pastTouchSlop) {
                zoom *= zoomChange
                pan += panChange

                val centroidSize = event.calculateCentroidSize(useCurrent = false)
                val zoomMotion = abs(1 - zoom) * centroidSize
                val panMotion = pan.getDistance()

                if (zoomMotion > touchSlop ||
                    panMotion > touchSlop
                ) {
                    pastTouchSlop = true
                    channel.trySend(TransformEvent.TransformStarted)
                }
            }

            if (pastTouchSlop) {
                if (zoomChange != 1f ||
                    panChange != Offset.Zero
                ) {
                    channel.trySend(
                        TransformEvent.TransformDelta(
                            zoomChange,
                            panChange,
                        )
                    )
                }
//                event.changes.fastForEach {
//                    if (it.positionChanged()) {
//                        it.consume()
//                    }
//                }
            }
        }
    } while (!canceled && event.changes.fastAny { it.pressed })
}
