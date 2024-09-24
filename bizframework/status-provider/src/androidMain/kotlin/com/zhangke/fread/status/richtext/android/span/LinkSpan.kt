package com.zhangke.fread.status.richtext.android.span

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.zhangke.fread.status.richtext.model.RichLinkTarget

typealias OnLinkTargetClick = (Context, RichLinkTarget) -> Unit

class LinkSpan(
    val linkTarget: RichLinkTarget,
) : ClickableSpan() {

    override fun onClick(widget: View) {}

    override fun updateDrawState(tp: TextPaint) {
        tp.setColor(tp.linkColor)
    }
}