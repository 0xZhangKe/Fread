package com.zhangke.fread.status.ui.richtext.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.text.Spanned
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.widget.TextView
import com.zhangke.fread.status.richtext.android.span.LinkSpan

class ClickableLinksDelegate(private val view: TextView) {
    private var onLinkSpanClick: OnLinkSpanClick? = null

    private val hlPaint = Paint()
    private var hlPath: Path? = null
    private var selectedSpan: LinkSpan? = null

    private val gestureDetector: GestureDetector

    init {
        hlPaint.isAntiAlias = true
        hlPaint.setPathEffect(CornerPathEffect(dp(view.context, 3f).toFloat()))
        hlPaint.style = Paint.Style.FILL_AND_STROKE
        hlPaint.strokeWidth = dp(view.context, 4f).toFloat()
        gestureDetector = GestureDetector(view.context, LinkGestureListener(), view.handler)
    }

    fun setOnLinkSpanClick(onLinkSpanClick: OnLinkSpanClick?) {
        this.onLinkSpanClick = onLinkSpanClick
    }

    fun onTouch(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_CANCEL) {
            // the gestureDetector does not provide a callback for CANCEL, therefore:
            // remove background color of view before passing event to gestureDetector
            resetAndInvalidate()
        }
        return gestureDetector.onTouchEvent(event)
    }

    /**
     * remove highlighting from span and let the system redraw the view
     */
    private fun resetAndInvalidate() {
        hlPath = null
        selectedSpan = null
        view.invalidate()
    }

    fun onDraw(canvas: Canvas) {
        hlPath?.let { hlPath ->
            canvas.save()
            canvas.translate(view.totalPaddingLeft.toFloat(), view.totalPaddingTop.toFloat())
            canvas.drawPath(hlPath, hlPaint)
            canvas.restore()
        }
    }

    /**
     * GestureListener for spans that represent URLs.
     * onDown: on start of touch event, set highlighting
     * onSingleTapUp: when there was a (short) tap, call onClick and reset highlighting
     * onLongPress: copy URL to clipboard, let user know, reset highlighting
     */
    private inner class LinkGestureListener : SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            val padLeft = view.totalPaddingLeft
            val padRight = view.totalPaddingRight
            val padTop = view.totalPaddingTop
            val padBottom = view.totalPaddingBottom
            var x = event.x
            var y = event.y
            if (x < padLeft || y < padTop || x > view.width - padRight || y > view.height - padBottom) return false
            x -= padLeft.toFloat()
            y -= padTop.toFloat()
            val l = view.layout
            val line = l.getLineForVertical(Math.round(y))
            val position = l.getOffsetForHorizontal(line, x)

            val text = view.text
            if (text is Spanned) {
                val s = text
                val spans = s.getSpans(0, s.length - 1, LinkSpan::class.java)
                for (span in spans) {
                    val start = s.getSpanStart(span)
                    val end = s.getSpanEnd(span)
                    if (position in start..<end) {
                        selectedSpan = span
                        hlPath = Path()
                        l.getSelectionPath(start, end, hlPath)
                        hlPaint.color = 0x33000000
                        view.invalidate()
                        return true
                    }
                }
            }
            return super.onDown(event)
        }

        override fun onSingleTapUp(event: MotionEvent): Boolean {
            selectedSpan?.let { span ->
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onLinkSpanClick?.onClick(span)
                resetAndInvalidate()
                return true
            }
            return false
        }

        override fun onLongPress(event: MotionEvent) {
        }
    }

    private fun dp(context: Context, dp: Float): Int {
        return Math.round(dp * context.resources.displayMetrics.density)
    }

    fun interface OnLinkSpanClick {
        fun onClick(span: LinkSpan)
    }
}
