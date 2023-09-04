package com.zhangke.framework.composable.transformable

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.coroutineScope

/**
 * State of [transformable]. Allows for a granular control of how different gesture
 * transformations are consumed by the user as well as to write custom transformation methods
 * using [transform] suspend function.
 */
interface TransformableCleverlyState {
    /**
     * Call this function to take control of transformations and gain the ability to send transform
     * events via [TransformCleverlyScope.transformBy]. All actions that change zoom, pan or rotation
     * values must be performed within a [transform] block (even if they don't call any other
     * methods on this object) in order to guarantee that mutual exclusion is enforced.
     *
     * If [transform] is called from elsewhere with the [transformPriority] higher or equal to
     * ongoing transform, ongoing transform will be canceled.
     */
    suspend fun transform(
        transformPriority: MutatePriority = MutatePriority.Default,
        block: suspend TransformCleverlyScope.() -> Unit
    )

    /**
     * Whether this [TransformableCleverlyState] is currently transforming by gesture or programmatically or
     * not.
     */
    val isTransformInProgress: Boolean
}

/**
 * Scope used for suspending transformation operations
 */
interface TransformCleverlyScope {
    /**
     * Attempts to transform by [zoomChange] in relative multiplied value, by [panChange] in
     * pixels.
     *
     * @param zoomChange scale factor multiplier change for zoom
     * @param panChange panning offset change, in [Offset] pixels
     */
    fun transformBy(
        zoomChange: Float = 1f,
        panChange: Offset = Offset.Zero,
    )
}

/**
 * Default implementation of [TransformableCleverlyState] interface that contains necessary information
 * about the ongoing transformations and provides smooth transformation capabilities.
 *
 * This is the simplest way to set up a [transformable] modifier. When constructing this
 * [TransformableCleverlyState], you must provide a [onTransformation] lambda, which will be invoked
 * whenever pan, zoom or rotation happens (by gesture input or any [TransformableCleverlyState.transform]
 * call) with the deltas from the previous event.
 *
 * @param onTransformation callback invoked when transformation occurs. The callback receives the
 * change from the previous event. It's relative scale multiplier for zoom, [Offset] in pixels
 * for pan and degrees for rotation. Callers should update their state in this lambda.
 */
fun TransformableClverlyState(
    onTransformation: (zoomChange: Float, panChange: Offset) -> Unit
): TransformableCleverlyState = DefaultTransformableCleverlyState(onTransformation)

/**
 * Create and remember default implementation of [TransformableCleverlyState] interface that contains
 * necessary information about the ongoing transformations and provides smooth transformation
 * capabilities.
 *
 * This is the simplest way to set up a [transformable] modifier. When constructing this
 * [TransformableCleverlyState], you must provide a [onTransformation] lambda, which will be invoked
 * whenever pan, zoom or rotation happens (by gesture input or any [TransformableCleverlyState.transform]
 * call) with the deltas from the previous event.
 *
 * @param onTransformation callback invoked when transformation occurs. The callback receives the
 * change from the previous event. It's relative scale multiplier for zoom, [Offset] in pixels
 * for pan and degrees for rotation. Callers should update their state in this lambda.
 */
@Composable
fun rememberTransformableCleverlyState(
    onTransformation: (zoomChange: Float, panChange: Offset) -> Unit
): TransformableCleverlyState {
    val lambdaState = rememberUpdatedState(onTransformation)
    return remember { TransformableClverlyState { z, p -> lambdaState.value.invoke(z, p) } }
}

/**
 * Animate zoom by a ratio of [zoomFactor] over the current size and suspend until its finished.
 *
 * @param zoomFactor ratio over the current size by which to zoom. For example, if [zoomFactor]
 * is `3f`, zoom will be increased 3 fold from the current value.
 * @param animationSpec [AnimationSpec] to be used for animation
 */
suspend fun TransformableCleverlyState.animateZoomBy(
    zoomFactor: Float,
    animationSpec: AnimationSpec<Float> = SpringSpec(stiffness = Spring.StiffnessLow)
) {
    require(zoomFactor > 0) {
        "zoom value should be greater than 0"
    }
    var previous = 1f
    transform {
        AnimationState(initialValue = previous).animateTo(zoomFactor, animationSpec) {
            val scaleFactor = if (previous == 0f) 1f else this.value / previous
            transformBy(zoomChange = scaleFactor)
            previous = this.value
        }
    }
}

/**
 * Animate pan by [offset] Offset in pixels and suspend until its finished
 *
 * @param offset offset to pan, in pixels
 * @param animationSpec [AnimationSpec] to be used for pan animation
 */
suspend fun TransformableCleverlyState.animatePanBy(
    offset: Offset,
    animationSpec: AnimationSpec<Offset> = SpringSpec(stiffness = Spring.StiffnessLow)
) {
    var previous = Offset.Zero
    transform {
        AnimationState(
            typeConverter = Offset.VectorConverter,
            initialValue = previous
        )
            .animateTo(offset, animationSpec) {
                val delta = this.value - previous
                transformBy(panChange = delta)
                previous = this.value
            }
    }
}

/**
 * Zoom without animation by a ratio of [zoomFactor] over the current size and suspend until it's
 * set.
 *
 * @param zoomFactor ratio over the current size by which to zoom
 */
suspend fun TransformableCleverlyState.zoomBy(zoomFactor: Float) = transform {
    transformBy(zoomFactor, Offset.Zero)
}

/**
 * Pan without animation by a [offset] Offset in pixels and suspend until it's set.
 *
 * @param offset offset in pixels by which to pan
 */
suspend fun TransformableCleverlyState.panBy(offset: Offset) = transform {
    transformBy(1f, offset)
}

/**
 * Stop and suspend until any ongoing [TransformableCleverlyState.transform] with priority
 * [terminationPriority] or lower is terminated.
 *
 * @param terminationPriority transformation that runs with this priority or lower will be stopped
 */
suspend fun TransformableCleverlyState.stopTransformation(
    terminationPriority: MutatePriority = MutatePriority.Default
) {
    this.transform(terminationPriority) {
        // do nothing, just lock the mutex so other scroll actors are cancelled
    }
}

private class DefaultTransformableCleverlyState(
    val onTransformation: (zoomChange: Float, panChange: Offset) -> Unit
) : TransformableCleverlyState {

    private val transformCleverlyScope: TransformCleverlyScope = object : TransformCleverlyScope {
        override fun transformBy(zoomChange: Float, panChange: Offset) =
            onTransformation(zoomChange, panChange)
    }

    private val transformMutex = MutatorMutex()

    private val isTransformingState = mutableStateOf(false)

    override suspend fun transform(
        transformPriority: MutatePriority,
        block: suspend TransformCleverlyScope.() -> Unit
    ): Unit = coroutineScope {
        transformMutex.mutateWith(transformCleverlyScope, transformPriority) {
            isTransformingState.value = true
            try {
                block()
            } finally {
                isTransformingState.value = false
            }
        }
    }

    override val isTransformInProgress: Boolean
        get() = isTransformingState.value
}
