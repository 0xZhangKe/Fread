package com.zhangke.framework.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.zhangke.framework.utils.pxToDp

val Animatable<Float, AnimationVector1D>.dpValue: Dp
    @Composable get() {
        return value.pxToDp(LocalDensity.current)
    }
