package com.zhangke.framework.composable.image.viewer

import androidx.compose.animation.core.AnimationScope
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.exponentialDecay
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
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.unit.Velocity
import com.zhangke.framework.composable.Bounds
import com.zhangke.framework.composable.toOffset
import com.zhangke.framework.utils.equalsExactly

@Composable
fun rememberImageViewerState(
    aspectRatio: Float,
    needAnimateIn: Boolean,
    initialSize: Size = Size.Unspecified,
    initialOffset: Offset = Offset.Unspecified,
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
            needAnimateIn = needAnimateIn,
        )
    }
}

@Stable
class ImageViewerState(
    private val aspectRatio: Float,
    private val initialSize: Size,
    private val initialOffset: Offset,
    private val needAnimateIn: Boolean,
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

    var onDismissRequest: (() -> Unit)? = null

    var onStartDismiss: (() -> Unit)? = null

    val exceed: Boolean get() = !_currentWidthPixel.floatValue.equalsExactly(layoutSize.width)

    private var alreadyAnimationIn = false

    private var flingAnimation: AnimationScope<Offset, AnimationVector2D>? = null
    private var scaleAnimation: AnimationScope<Float, AnimationVector1D>? = null
    private var resumeOffsetYAnimation: AnimationScope<Float, AnimationVector1D>? = null

    private val draggableBounds: Bounds
        get() {
            return calculateDragBounds()
        }

    init {
        if (initialSize != Size.Unspecified) {
            _currentWidthPixel.floatValue = initialSize.width
            _currentHeightPixel.floatValue = initialSize.height
        }
        if (!initialOffset.isUnspecified) {
            _currentOffsetXPixel.floatValue = initialOffset.x
            _currentOffsetYPixel.floatValue = initialOffset.y
        }
    }

    private fun calculateDragBounds(): Bounds {
        val imageWidth = _currentWidthPixel.floatValue
        val imageHeight = _currentHeightPixel.floatValue
        val left: Float
        val right: Float
        if (imageWidth > layoutSize.width) {
            left = -(imageWidth - layoutSize.width)
            right = 0F
        } else {
            left = (layoutSize.width - imageWidth) / 2F
            right = left
        }
        val top: Float
        val bottom: Float
        if (imageHeight > layoutSize.height) {
            top = -(imageHeight - layoutSize.height)
            bottom = 0F
        } else {
            top = (layoutSize.height - imageHeight) / 2F
            bottom = top
        }
        return Bounds(
            left = left,
            top = top,
            right = right,
            bottom = bottom,
        )
    }

    suspend fun updateLayoutSize(size: Size) {
        layoutSize = size
        onLayoutSizeChanged()
        onAnimateInFinished?.invoke()
    }

    private suspend fun onLayoutSizeChanged() {
        if (initialSize.isUnspecified || initialOffset.isUnspecified) {
            _currentWidthPixel.floatValue = standardWidth
            _currentHeightPixel.floatValue = standardHeight
            _currentOffsetXPixel.floatValue = 0F
            _currentOffsetYPixel.floatValue = layoutSize.height / 2F - standardHeight / 2F
        } else {
            if (needAnimateIn && !alreadyAnimationIn) {
                alreadyAnimationIn = true
                animateToStandard()
            } else {
                _currentWidthPixel.floatValue = standardWidth
                _currentHeightPixel.floatValue = standardHeight
                _currentOffsetXPixel.floatValue = 0F
                _currentOffsetYPixel.floatValue = layoutSize.height / 2F - standardHeight / 2F
            }
        }
    }

    suspend fun animateToStandard() {
        val layoutSize = layoutSize
        if (layoutSize == Size.Zero) return
        val targetWidth = standardWidth
        val targetHeight = standardHeight
        animateToTarget(
            targetWidth = targetWidth,
            targetHeight = targetHeight,
            targetOffsetX = 0F,
            targetOffsetY = layoutSize.height / 2F - targetHeight / 2F,
        )
    }

    suspend fun animateToBig(point: Offset) {
        val layoutSize = layoutSize
        if (layoutSize == Size.Zero) return

        val targetWidth = standardWidth * maximumScale
        val targetHeight = targetWidth / aspectRatio
        animateToTarget(
            targetWidth = targetWidth,
            targetHeight = targetHeight,
            targetOffsetX = 0F,
            targetOffsetY = layoutSize.height / 2F - targetHeight / 2F,
            center = point,
        )
    }

    fun drag(dragAmount: Offset) {
        cancelAnimation()
        if (exceed) {
            dragForVisit(dragAmount)
        } else {
            dragForExit(dragAmount)
        }
    }

    private fun dragForVisit(dragAmount: Offset) {
        val currentOffset = Offset(_currentOffsetXPixel.floatValue, _currentOffsetYPixel.floatValue)
        val newOffset = currentOffset + dragAmount
        val fixedOffset = draggableBounds.coerceIn(newOffset)
        _currentOffsetXPixel.floatValue = fixedOffset.x
        _currentOffsetYPixel.floatValue = fixedOffset.y
    }

    private fun dragForExit(dragAmount: Offset) {
        val dragAmountY = dragAmount.y
        if (dragAmountY <= 0F) {
            if (_currentOffsetYPixel.floatValue > 0F) {
                _currentOffsetYPixel.floatValue += dragAmountY
            }
        } else {
            _currentOffsetYPixel.floatValue += dragAmountY
        }
    }

    suspend fun dragStop(initialVelocity: Velocity) {
        cancelAnimation()
        if (!exceed) {
            dragStopForExit()
            return
        }
        val initialValue = Offset(_currentOffsetXPixel.floatValue, _currentOffsetYPixel.floatValue)
        AnimationState(
            typeConverter = Offset.VectorConverter,
            initialValue = initialValue,
            initialVelocity = initialVelocity.toOffset(),
        ).animateDecay(exponentialDecay()) {
            flingAnimation = this
            if (draggableBounds.outsideAbsolute(value) ||
                velocity.getDistance() <= 300
            ) {
                flingAnimation = null
                cancelAnimation()
                return@animateDecay
            }
            val progressOffset = draggableBounds.coerceIn(value)
            _currentOffsetXPixel.floatValue = progressOffset.x
            _currentOffsetYPixel.floatValue = progressOffset.y
        }
    }

    private suspend fun dragStopForExit() {
        cancelAnimation()
        val standardOffsetY = layoutSize.height / 2F - _currentHeightPixel.floatValue / 2F
        val totalAmount = _currentOffsetYPixel.floatValue - standardOffsetY
        val exitOffsetYThresholds = standardHeight * 0.3F
        if (totalAmount > exitOffsetYThresholds) {
            startDismiss()
        } else {
            val anim = AnimationState(initialValue = _currentOffsetYPixel.floatValue)
            anim.animateTo(
                targetValue = standardOffsetY,
                animationSpec = tween(durationMillis = ImageViewerDefault.ANIMATION_DURATION),
            ) {
                resumeOffsetYAnimation = this
                _currentOffsetYPixel.floatValue = value
            }
        }
    }

    internal suspend fun startDismiss() {
        onStartDismiss?.invoke()
        if ((initialSize.isUnspecified || initialSize.isEmpty()) && initialOffset.isUnspecified) {
            onDismissRequest?.invoke()
            return
        }
        val targetWidth =
            if (initialSize.isEmpty()) _currentWidthPixel.floatValue else initialSize.width
        val targetHeight =
            if (initialSize.isEmpty()) _currentHeightPixel.floatValue else initialSize.height
        val targetOffsetX =
            if (initialOffset.isUnspecified) _currentOffsetXPixel.floatValue else initialOffset.x
        val targetOffsetY =
            if (initialOffset.isUnspecified) _currentOffsetYPixel.floatValue else initialOffset.y
        animateToTarget(
            targetWidth = targetWidth,
            targetHeight = targetHeight,
            targetOffsetX = targetOffsetX,
            targetOffsetY = targetOffsetY,
        )
        onDismissRequest?.invoke()
    }

    private suspend fun animateToTarget(
        targetWidth: Float,
        targetHeight: Float,
        targetOffsetX: Float,
        targetOffsetY: Float,
        center: Offset? = null,
    ) {
        cancelAnimation()
        val startWidth = _currentWidthPixel.floatValue
        val startHeight = _currentWidthPixel.floatValue
        val startOffsetX = _currentOffsetXPixel.floatValue
        val startOffsetY = _currentOffsetYPixel.floatValue
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
                animationSpec = tween(durationMillis = ImageViewerDefault.ANIMATION_DURATION),
            ) {
                scaleAnimation = this
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

    private fun cancelAnimation() {
        scaleAnimation?.takeIf { it.isRunning }?.cancelAnimation()
        scaleAnimation = null
        flingAnimation?.takeIf { it.isRunning }?.cancelAnimation()
        flingAnimation = null
        resumeOffsetYAnimation?.takeIf { it.isRunning }?.cancelAnimation()
        resumeOffsetYAnimation = null
    }

    internal fun zoom(zoom: Float) {
        val newWidth = (_currentWidthPixel.floatValue * zoom).coerceInWidth()
        val newHeight = (_currentHeightPixel.floatValue * zoom).coerceInHeight()
        _currentWidthPixel.floatValue = newWidth
        _currentHeightPixel.floatValue = newHeight
        _currentOffsetYPixel.floatValue = layoutSize.height / 2F - newHeight / 2F
        if (newWidth == standardWidth) {
            _currentOffsetXPixel.floatValue = 0F
        }
    }

    internal fun inImageBound(position: Offset): Boolean {
        val yOffset = _currentOffsetYPixel.floatValue
        val xOffset = _currentOffsetXPixel.floatValue
        val height = _currentHeightPixel.floatValue
        val width = _currentWidthPixel.floatValue
        val yRange = yOffset..yOffset + height
        val xRange = xOffset..xOffset + width
        return position.x in xRange && position.y in yRange
    }

    private fun Float.coerceInWidth(): Float {
        val maxWidth = standardWidth * maximumScale
        return coerceAtLeast(standardWidth).coerceAtMost(maxWidth)
    }

    private fun Float.coerceInHeight(): Float {
        val maxHeight = standardHeight * maximumScale
        return coerceAtLeast(standardHeight).coerceAtMost(maxHeight)
    }

    internal companion object {

        private const val MAGIC_NUMBER = -21039142F

        private val Size.magicWidth: Float
            get() = if (isUnspecified) MAGIC_NUMBER else width

        private val Size.magicHeight: Float
            get() = if (isUnspecified) MAGIC_NUMBER else height

        private val Offset.magicX: Float
            get() = if (isUnspecified) MAGIC_NUMBER else x

        private val Offset.magicY: Float
            get() = if (isUnspecified) MAGIC_NUMBER else y

        private fun magicSize(width: Float, height: Float): Size {
            if (width == MAGIC_NUMBER || height == MAGIC_NUMBER) return Size.Unspecified
            return Size(width = width, height = height)
        }

        private fun magicOffset(x: Float, y: Float): Offset {
            if (x == MAGIC_NUMBER || y == MAGIC_NUMBER) return Offset.Unspecified
            return Offset(x = x, y = y)
        }

        val Saver: Saver<ImageViewerState, *> = listSaver(
            save = {
                listOf<Any>(
                    it.aspectRatio,
                    it.initialSize.magicWidth,
                    it.initialSize.magicHeight,
                    it.initialOffset.magicX,
                    it.initialOffset.magicY,
                    it.minimumScale,
                    it.maximumScale,
                    it.needAnimateIn,
                )
            },
            restore = {
                ImageViewerState(
                    aspectRatio = it[0] as Float,
                    initialSize = magicSize(width = it[1] as Float, height = it[2] as Float),
                    initialOffset = magicOffset(
                        x = it[3] as Float,
                        y = it[4] as Float,
                    ),
                    minimumScale = it[5] as Float,
                    maximumScale = it[6] as Float,
                    needAnimateIn = it[7] as Boolean,
                )
            }
        )
    }

    private fun Offset.coerceIn(bounds: Bounds): Offset {
        return Offset(
            x = this.x.coerceIn(bounds.left..bounds.right),
            y = this.y.coerceIn(bounds.top..bounds.bottom),
        )
    }
}

object ImageViewerDefault {

    const val ANIMATION_DURATION = 200
}
