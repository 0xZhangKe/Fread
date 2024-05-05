package com.zhangke.utopia.status.ui.richtext.android

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.widget.TextView
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView
import com.zhangke.utopia.status.richtext.RichText
import com.zhangke.utopia.status.richtext.android.span.CustomEmojiSpan
import com.zhangke.utopia.status.richtext.android.span.LinkSpan
import com.zhangke.utopia.status.richtext.android.span.OnLinkTargetClick
import com.zhangke.utopia.status.ui.richtext.LinkClickNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Composable
fun AndroidRichText(
    modifier: Modifier,
    richText: RichText,
    color: Color = Color.Unspecified,
    fontSp: Float = 14F,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onLinkTargetClick: OnLinkTargetClick,
) {
    val localContentColor = LocalContentColor.current
    val localContentAlpha = LocalContentAlpha.current
    val finalColor = if (color.isSpecified) {
        color
    } else {
        localContentColor.copy(localContentAlpha)
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    AndroidView(
        modifier = modifier,
        factory = {
            TextView(it).apply {
                this.gravity = Gravity.START
                this.movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = { textView ->
            textView.textSize = fontSp
            if (color != Color.Unspecified) {
                textView.setTextColor(finalColor.value.toInt())
            }
            textView.layoutDirection = when (layoutDirection) {
                LayoutDirection.Ltr -> android.view.View.LAYOUT_DIRECTION_LTR
                LayoutDirection.Rtl -> android.view.View.LAYOUT_DIRECTION_RTL
            }
            textView.maxLines = maxLines
            textView.minLines = minLines
            textView.ellipsize = when (overflow) {
                TextOverflow.Ellipsis -> android.text.TextUtils.TruncateAt.END
                else -> android.text.TextUtils.TruncateAt.START
            }

            val charSequence = richText.parse()
            textView.text = charSequence
            startLoadEmojiImage(
                coroutineScope = coroutineScope,
                context = context,
                textView = textView,
                charSequence = charSequence,
            )
            processLinkClick(
                charSequence = charSequence,
                onLinkTargetClick = onLinkTargetClick,
            )
        }
    )
}

private fun startLoadEmojiImage(
    coroutineScope: CoroutineScope,
    context: Context,
    textView: TextView,
    charSequence: CharSequence,
) {
    coroutineScope.launch {
        val customEmojiSpans = charSequence.getAllSpans<CustomEmojiSpan>()
        customEmojiSpans.map {
            async {
                it.loadDrawable(context)
                textView.invalidate()
            }
        }.awaitAll()
    }
}

private fun processLinkClick(
    charSequence: CharSequence,
    onLinkTargetClick: OnLinkTargetClick,
) {
    val linkSpans = charSequence.getAllSpans<LinkSpan>()
    linkSpans.forEach { linkSpan ->
        linkSpan.onLinkClick = onLinkTargetClick
    }
}

private inline fun <reified T> CharSequence.getAllSpans(): List<T> {
    val spans = (this as? SpannableStringBuilder) ?: return emptyList()
    return spans.getSpans(0, spans.length, T::class.java).toList()
}
