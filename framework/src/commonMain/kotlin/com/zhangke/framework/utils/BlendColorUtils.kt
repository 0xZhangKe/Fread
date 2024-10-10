package com.zhangke.framework.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.pow
import kotlin.math.round


object BlendColorUtils {

    fun blend(
        fraction: Float,
        startColor: Color,
        endColor: Color,
    ): Color {
        val startInt: Int = startColor.toArgb()
        val startA = ((startInt shr 24) and 0xff) / 255.0f
        var startR = ((startInt shr 16) and 0xff) / 255.0f
        var startG = ((startInt shr 8) and 0xff) / 255.0f
        var startB = (startInt and 0xff) / 255.0f

        val endInt: Int = endColor.toArgb()
        val endA = ((endInt shr 24) and 0xff) / 255.0f
        var endR = ((endInt shr 16) and 0xff) / 255.0f
        var endG = ((endInt shr 8) and 0xff) / 255.0f
        var endB = (endInt and 0xff) / 255.0f


        // convert from sRGB to linear
        startR = startR.pow(2.2f)
        startG = startG.pow(2.2f)
        startB = startB.pow(2.2f)

        endR = endR.pow(2.2f)
        endG = endG.pow(2.2f)
        endB = endB.pow(2.2f)


        // compute the interpolated color in linear space
        var a = startA + fraction * (endA - startA)
        var r = startR + fraction * (endR - startR)
        var g = startG + fraction * (endG - startG)
        var b = startB + fraction * (endB - startB)

        // convert back to sRGB in the [0..255] range
        a *= 255.0f
        r = r.pow(1.0f / 2.2f) * 255.0f
        g = g.pow(1.0f / 2.2f) * 255.0f
        b = b.pow(1.0f / 2.2f) * 255.0f

        return Color(
            round(r).toInt(),
            round(g).toInt(),
            round(b).toInt(),
            round(a).toInt(),
        )
    }
}
