package com.zhangke.framework.composable.image.viewer

import android.util.Log
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.zhangke.framework.composable.isZero
import com.zhangke.framework.utils.equalsExactly

private const val animationDuration = 500

@Composable
fun rememberImageViewerState(
    aspectRatio: Float,
    initialSize: Size = Size.Unspecified,
    initialOffset: Offset = Offset.Zero,
    minimumScale: Float = 1f,
    maximumScale: Float = 3f,
): ImageViewerState {
    return rememberSaveable(saver = ImageViewerState.Saver) {
        ImageViewerState(
            aspectRatio = aspectRatio,
            initialSize = initialSize,
            initialOffset = initialOffset,
            minimumScale = minimumScale,
            maximumScale = maximumScale,
        )
    }
}

@Stable
class ImageViewerState(
    private val aspectRatio: Float,
    private val initialSize: Size,
    private val initialOffset: Offset,
    private val minimumScale: Float = 1f,
    private val maximumScale: Float = 3f,
) {

    private var _currentWidthPixel = mutableFloatStateOf(0F)
    private var _currentHeightPixel = mutableFloatStateOf(0F)
    private var _currentOffsetXPixel = mutableFloatStateOf(0F)
    private var _currentOffsetYPixel = mutableFloatStateOf(0F)

    val currentWidthPixel: Float by _currentWidthPixel
    val currentHeightPixel: Float by _currentHeightPixel
    val currentOffsetXPixel: Float by _currentOffsetXPixel
    val currentOffsetYPixel: Float by _currentOffsetYPixel

    private var layoutSize: Size = Size.Zero
    private val standardWidth: Float get() = layoutSize.width
    private val standardHeight: Float get() = standardWidth / aspectRatio

    var onAnimateInFinished: (() -> Unit)? = null

    val scaled: Boolean get() = !_currentWidthPixel.floatValue.equalsExactly(layoutSize.width)

    init {
        if (initialSize != Size.Unspecified) {
            _currentWidthPixel.floatValue = initialSize.width
            _currentHeightPixel.floatValue = initialSize.height
        }
        if (!initialOffset.isZero) {
            _currentOffsetXPixel.floatValue = initialOffset.x
            _currentOffsetYPixel.floatValue = initialOffset.y
        }
    }

    suspend fun updateLayoutSize(size: Size) {
        layoutSize = size
        onLayoutSizeChanged()
        onAnimateInFinished?.invoke()
        Log.d("U_TEST", "updateLayoutSize:$size")
    }

    private suspend fun onLayoutSizeChanged() {
        animateToStandard()
    }

    suspend fun animateToStandard() {
        val layoutSize = layoutSize
        if (layoutSize == Size.Zero) return
        val targetWidth = standardWidth
        val targetHeight = standardHeight
        animateToTargetInCenter(
            targetWidth = targetWidth,
            targetHeight = targetHeight,
        )
    }

    suspend fun animateToBig() {
        val layoutSize = layoutSize
        if (layoutSize == Size.Zero) return
        val startWidth = _currentWidthPixel.floatValue
        val startHeight = _currentWidthPixel.floatValue
        val targetWidth = standardWidth * maximumScale
        val targetHeight = targetWidth / aspectRatio
        val widthDiff = targetWidth - startWidth
        val heightDiff = targetHeight - startHeight
        val anim = AnimationState(initialValue = 0f)
        anim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDuration),
        ) {
            val progress = value
            if (widthDiff != 0F) {
                _currentWidthPixel.floatValue = startWidth + widthDiff * progress
            }
            if (heightDiff != 0F) {
                _currentHeightPixel.floatValue = startHeight + heightDiff * progress
            }
        }
    }

    private suspend fun animateToTargetInCenter(
        targetWidth: Float,
        targetHeight: Float,
    ) {
        val startWidth = _currentWidthPixel.floatValue
        val startHeight = _currentWidthPixel.floatValue
        val startOffsetX = _currentOffsetXPixel.floatValue
        val startOffsetY = _currentOffsetYPixel.floatValue
        val targetOffsetX = 0F
        val targetOffsetY = layoutSize.height / 2F - targetHeight / 2F
        Log.d(
            "U_TEST",
            "height: $startHeight -> $targetHeight, offsetY: $startOffsetY -> $targetOffsetY",
        )
        if (startWidth != targetWidth || startHeight != targetHeight
            || startOffsetX != targetOffsetX || startOffsetY != targetOffsetY
        ) {
            val widthDiff = targetWidth - startWidth
            val heightDiff = targetHeight - startHeight
            val offsetXDiff = targetOffsetX - startOffsetX
            val offsetYDiff = targetOffsetY - startOffsetY
            val anim = AnimationState(initialValue = 0f)
            anim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = animationDuration),
            ) {
                val progress = value
                if (widthDiff != 0F) {
                    _currentWidthPixel.floatValue = startWidth + widthDiff * progress
                }
                if (heightDiff != 0F) {
                    _currentHeightPixel.floatValue = startHeight + heightDiff * progress
                }
                if (offsetXDiff != 0F) {
                    _currentOffsetXPixel.floatValue = startOffsetX + offsetXDiff * progress
                }
                if (offsetYDiff != 0F) {
                    _currentOffsetYPixel.floatValue = startOffsetY + offsetYDiff * progress
                }
            }
        }
    }

    internal companion object {

        val Saver: Saver<ImageViewerState, *> = listSaver(
            save = {
                listOf(
                    it.aspectRatio,
                    it.initialSize.height,
                    it.initialSize.width,
                    it.initialOffset.x,
                    it.initialOffset.y,
                    it.minimumScale,
                    it.maximumScale,
                )
            },
            restore = {
                ImageViewerState(
                    aspectRatio = it[0],
                    initialSize = Size(width = it[1], height = it[2]),
                    initialOffset = Offset(
                        x = it[3],
                        y = it[4],
                    ),
                    minimumScale = it[5],
                    maximumScale = it[6],
                )
            }
        )
    }
}
