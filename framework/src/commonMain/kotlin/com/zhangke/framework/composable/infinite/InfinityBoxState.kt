package com.zhangke.framework.composable.infinite

import androidx.compose.animation.core.AnimationScope
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Velocity
import com.zhangke.framework.composable.Bounds
import com.zhangke.framework.composable.toOffset

@Composable
fun rememberInfinityBoxState(): InfinityBoxState {
    return rememberSaveable(saver = InfinityBoxState.Saver) {
        InfinityBoxState()
    }
}

@Stable
class InfinityBoxState {

    var exceed: Boolean by mutableStateOf(false)

    private val _currentOffset = mutableStateOf(Offset.Zero)
    val currentOffset by _currentOffset

    internal var layoutSize = Size.Zero

    internal var draggableBounds = Bounds.EMPTY
        set(value) {
            cancelAnimation()
            field = value
            _currentOffset.value = Offset.Zero
        }

    private var flingAnimation: AnimationScope<Offset, AnimationVector2D>? = null

    fun moveToCenter() {
        cancelAnimation()
        if (!exceed) return
        _currentOffset.value = Offset(
            x = draggableBounds.left / 2F,
            y = draggableBounds.top / 2F,
        )
    }

    fun drag(dragAmount: Offset) {
        cancelAnimation()
        if (!exceed) return
        val newOffset = currentOffset + dragAmount
        _currentOffset.value = draggableBounds.coerceIn(newOffset)
    }

    suspend fun fling(initialVelocity: Velocity) {
        if (!exceed) return
        val initialValue = currentOffset
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
            _currentOffset.value = draggableBounds.coerceIn(value)
        }
    }

    private fun cancelAnimation() {
        val animation = flingAnimation ?: return
        flingAnimation = null
        if (!animation.isRunning) return
        animation.cancelAnimation()
    }

    companion object {

        val Saver: Saver<InfinityBoxState, *> = Saver(
            save = {
                emptyArray<Unit>()
            },
            restore = {
                InfinityBoxState()
            },
        )
    }
}
