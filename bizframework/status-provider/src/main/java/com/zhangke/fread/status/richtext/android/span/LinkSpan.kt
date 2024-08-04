package com.zhangke.fread.status.richtext.android.span

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention

typealias OnLinkTargetClick = (Context, LinkSpan.LinkTarget) -> Unit

class LinkSpan(
    val linkTarget: LinkTarget,
) : ClickableSpan() {

    override fun onClick(widget: View) {}

    override fun updateDrawState(tp: TextPaint) {
        tp.setColor(tp.linkColor)
    }

    sealed interface LinkTarget {

        data class UrlTarget(val url: String) : LinkTarget

        data class MentionTarget(val mention: Mention) : LinkTarget

        data class HashtagTarget(val hashtag: HashtagInStatus) : LinkTarget

        data class MaybeHashtagTarget(val hashtag: String) : LinkTarget
    }
}