package com.zhangke.utopia.status.richtext.android.span

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention

typealias OnLinkTargetClick = (Context, LinkSpan.LinkTarget) -> Unit

class LinkSpan(
    private val linkTarget: LinkTarget,
) : ClickableSpan() {

    var onLinkClick: (OnLinkTargetClick)? = null

    override fun onClick(widget: View) {
        onLinkClick?.invoke(widget.context, linkTarget)
    }

    override fun updateDrawState(tp: TextPaint) {
        tp.setColor(tp.linkColor)
    }

    sealed interface LinkTarget {

        data class UrlTarget(val url: String) : LinkTarget

        data class MentionTarget(val mention: Mention) : LinkTarget

        data class HashtagTarget(val hashtag: HashtagInStatus) : LinkTarget
    }
}