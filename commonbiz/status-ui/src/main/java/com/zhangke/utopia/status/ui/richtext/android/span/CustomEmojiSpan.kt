package com.zhangke.utopia.status.ui.richtext.android.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.status.model.Emoji
import kotlinx.coroutines.CoroutineScope

class CustomEmojiSpan(
    private val emoji: Emoji,

) : ReplacementSpan() {

    private var drawable: Drawable? = null

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return Math.round(paint.descent() - paint.ascent())
    }

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
        val drawable = drawable
        val size = Math.round(paint.descent() - paint.ascent())
        if (drawable == null) {
            val alpha = paint.alpha
            paint.setAlpha(alpha shr 1)
            val radius = Math.round(2 * appContext.resources.displayMetrics.density).toFloat()
            canvas.drawRoundRect(
                x,
                top.toFloat(),
                x + size,
                (top + size).toFloat(),
                radius,
                radius,
                paint
            )
            paint.setAlpha(alpha)
        } else {
            // AnimatedImageDrawable doesn't like when its bounds don't start at (0, 0)
            val bounds = drawable.getBounds()
            val dw = drawable.intrinsicWidth
            val dh = drawable.intrinsicHeight
            if (bounds.left != 0 || bounds.top != 0 || bounds.right != dw || bounds.left != dh) {
                drawable.setBounds(0, 0, dw, dh)
            }
            canvas.save()
            canvas.translate(x, top.toFloat())
            canvas.scale(size / dw.toFloat(), size / dh.toFloat(), 0f, 0f)
            drawable.draw(canvas)
            canvas.restore()
        }
    }
}
