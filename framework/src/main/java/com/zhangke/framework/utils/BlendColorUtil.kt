package com.zhangke.framework.utils

import android.animation.ArgbEvaluator
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object BlendColorUtil {

    private val argbEvaluator = ArgbEvaluator()

    fun blend(
        fraction: Float,
        startColor: Color,
        endColor: Color,
    ): Color {
        val colorValue = argbEvaluator.evaluate(
            fraction,
            startColor.toArgb(),
            endColor.toArgb()
        )
        return Color(colorValue as Int)
    }
}
