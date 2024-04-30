package com.zhangke.utopia.status.ui.richtext.android.span

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.Mention

class LinkSpan(
    private val linkTarget: LinkTarget,
): ClickableSpan() {

    override fun onClick(widget: View) {
        Log.d("U_TEST", "LinkSpan onClick: $linkTarget")
    }

    override fun updateDrawState(tp: TextPaint) {
        tp.setColor(tp.linkColor)
    }

    sealed interface LinkTarget{

        data class UrlTarget(val url: String): LinkTarget

        data class MentionTarget(val mention: Mention): LinkTarget

        data class HashtagTarget(val hashtag: Hashtag): LinkTarget
    }
}