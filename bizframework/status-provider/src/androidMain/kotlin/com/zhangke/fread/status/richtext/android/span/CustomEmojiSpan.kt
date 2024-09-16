package com.zhangke.fread.status.richtext.android.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import androidx.core.graphics.drawable.toDrawable
import com.seiko.imageloader.imageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.ImageResult
import com.zhangke.framework.utils.appContext
import com.zhangke.fread.status.model.Emoji
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomEmojiSpan(private val emoji: Emoji) : ReplacementSpan() {

    val needLoad: Boolean get() = drawable == null && !loading

    private var loading = false
    private var drawable: Drawable? = null

    suspend fun loadDrawable(context: Context): Boolean = withContext(Dispatchers.IO) {
        loading = true
        val request = ImageRequest(emoji.url)
        val result = context.imageLoader.execute(request)
        loading = false

        var success = false
        if (result is ImageResult.OfImage) {
            success = true
            this@CustomEmojiSpan.drawable = result.image.drawable
        } else if (result is ImageResult.OfBitmap) {
            success = true
            this@CustomEmojiSpan.drawable = result.bitmap.toDrawable(context.resources)
        }
        success
    }

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
