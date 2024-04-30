package com.zhangke.utopia.status.ui.richtext.android.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

class InvisibleSpan: ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int = 0

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
    }
}