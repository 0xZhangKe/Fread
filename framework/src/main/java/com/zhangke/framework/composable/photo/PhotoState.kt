package com.zhangke.framework.composable.photo

import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.times

@ExperimentalPhotoApi
@Composable
fun rememberPhotoState(
    initialScale: Float = 1f,
    initialOffset: Offset = Offset.Zero,
    minimumScale: Float = 1f,
    maximumScale: Float = 3f,
): PhotoState = rememberSaveable(saver = PhotoState.Saver) {
    PhotoState(
        initialScale = initialScale,
        initialOffset = initialOffset,
        minimumScale = minimumScale,
        maximumScale = maximumScale,
    )
}

@ExperimentalPhotoApi
@Stable
class PhotoState(
    @FloatRange(from = 1.0) private val initialScale: Float = 1f,
    private val initialOffset: Offset = Offset.Zero,
    private val minimumScale: Float = 1f,
    internal val maximumScale: Float = 3f,
) {

    internal var layoutSize: Size = Size.Zero

    private var photoIntrinsicSize: Size = Size.Unspecified

    fun setPhotoIntrinsicSize(size: Size) {
        photoIntrinsicSize = size
    }

    private var _currentScale by mutableStateOf(initialScale)

    @get:FloatRange(from = 1.0)
    internal var currentScale: Float
        get() = _currentScale
        internal set(value) {
            val coerceValue = value.coerceIn(minimumScale, maximumScale)
            if (coerceValue != _currentScale) {
                _currentScale = coerceValue
            }
        }

    private var _currentOffset by mutableStateOf(initialOffset)

    internal var currentOffset: Offset
        get() = _currentOffset
        internal set(value) {
            val (scrollableX, scrollableY) = calculateScrollableBounds()
            val coerceValue = Offset(
                value.x.coerceIn(-scrollableX, scrollableX),
                value.y.coerceIn(-scrollableY, scrollableY),
            )
            if (coerceValue != _currentOffset) {
                _currentOffset = coerceValue
            }
        }

    private fun calculateScrollableBounds(): Offset {
        val content = if (photoIntrinsicSize.isSpecified) {
            val contentScale = ContentScale.Fit
            photoIntrinsicSize * contentScale.computeScaleFactor(photoIntrinsicSize, layoutSize)
        } else {
            layoutSize
        }
        return Offset(
            x = ((content.width * currentScale - layoutSize.width) / 2).coerceAtLeast(0f),
            y = ((content.height * currentScale - layoutSize.height) / 2).coerceAtLeast(0f),
        )
    }

    val isScaled: Boolean
        get() {
            return currentScale != 1F || !currentOffset.isZero
        }

    private val Offset.isZero: Boolean get() = x == 0F && y == 0F

    /**
     * Animate to the initial state.
     */
    suspend fun animateToInitialState() {
        animateToTarget(initialScale, initialOffset)
    }

    suspend fun animateToCenter() {
        animateToTarget(1F, initialOffset)
    }

    private suspend fun animateToTarget(
        targetScale: Float,
        targetOffset: Offset,
    ) {
        val startScale = currentScale
        val startOffset = currentOffset
        if (startScale != targetScale || startOffset != targetOffset) {
            val scaleDiff = targetScale - startScale
            val offsetDiff = targetOffset - startOffset
            val anim = AnimationState(initialValue = 0f)
            anim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500),
            ) {
                currentScale = startScale + scaleDiff * value
                currentOffset = startOffset + offsetDiff * value
            }
        }
    }

    /**
     * Animate to the given scale to the center of the layout.
     *
     * @param scale the scale to animate to. Must be between 1f and [maximumScale] (inclusive).
     */
    suspend fun animateScale(
        @FloatRange(from = 1.0) scale: Float,
    ) {
        val initialScale = currentScale
        if (initialScale != scale) {
            val diff = scale - initialScale
            val anim = AnimationState(initialValue = 0f)
            anim.animateTo(targetValue = 1f) {
                currentScale = initialScale + diff * value
            }
        }
    }

    internal suspend fun performFling(
        initialVelocity: Offset,
        decay: DecayAnimationSpec<Offset> = exponentialDecay(),
    ) {
        val initialValue = currentOffset
        val anim = AnimationState(
            typeConverter = Offset.VectorConverter,
            initialValue = initialValue,
            initialVelocity = initialVelocity,
        )
        anim.animateDecay(decay) {
            currentOffset = value

            if (isOutOfBounds(value) || velocity.getDistance() <= 3000) {
                cancelAnimation()
            }
        }
    }

    private fun isOutOfBounds(offset: Offset): Boolean {
        val (scrollableX, scrollableY) = calculateScrollableBounds()
        return offset.x !in -scrollableX..scrollableX && offset.y !in (-scrollableY..scrollableY)
    }

    internal companion object {
        /**
         * The default [Saver] implementation for [PhotoState].
         */
        val Saver: Saver<PhotoState, *> = listSaver(
            save = {
                listOf<Any>(
                    it.currentScale,
                    it.currentOffset.x,
                    it.currentOffset.y,
                    it.minimumScale,
                    it.maximumScale,
                )
            },
            restore = {
                PhotoState(
                    initialScale = it[0] as Float,
                    initialOffset = Offset(
                        x = it[1] as Float,
                        y = it[2] as Float,
                    ),
                    minimumScale = it[3] as Float,
                    maximumScale = it[4] as Float,
                )
            }
        )
    }
}