package com.zhangke.fread.status.ui.richtext.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.zhangke.fread.status.richtext.android.span.LinkSpan

@SuppressLint("AppCompatCustomView")
class LinkedTextView(
    context: Context,
) : TextView(context) {

    var onLinkSpanClick: ((LinkSpan) -> Unit)? = null
        set(value) {
            field = value
            clickableLinksDelegate.setOnLinkSpanClick { value?.invoke(it) }
        }

    private val clickableLinksDelegate = ClickableLinksDelegate(
        this
    ).apply {
        if (onLinkSpanClick != null) {
            setOnLinkSpanClick { onLinkSpanClick?.invoke(it) }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null && clickableLinksDelegate.onTouch(event)) return true
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        clickableLinksDelegate.onDraw(canvas)
    }
}
