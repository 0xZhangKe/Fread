package com.zhangke.utopia.status.ui.richtext.android

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView
import com.zhangke.utopia.status.richtext.RichText
import com.zhangke.utopia.status.richtext.android.span.CustomEmojiSpan
import com.zhangke.utopia.status.richtext.android.span.OnLinkTargetClick
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
    textSelectable: Boolean = false,
    onLinkTargetClick: OnLinkTargetClick,
) {
    if (textSelectable) {
        SelectableTextView(
            modifier = modifier,
            richText = richText,
            finalColor = color,
            fontSp = fontSp,
            layoutDirection = layoutDirection,
            overflow = overflow,
            maxLines = maxLines,
            minLines = minLines,
            onLinkTargetClick = onLinkTargetClick,
        )
    } else {
        UnSelectableTextView(
            modifier = modifier,
            richText = richText,
            finalColor = color,
            fontSp = fontSp,
            layoutDirection = layoutDirection,
            overflow = overflow,
            maxLines = maxLines,
            minLines = minLines,
            onLinkTargetClick = onLinkTargetClick,
        )
    }
}

@Composable
private fun UnSelectableTextView(
    modifier: Modifier,
    richText: RichText,
    finalColor: Color,
    fontSp: Float,
    layoutDirection: LayoutDirection,
    overflow: TextOverflow,
    maxLines: Int,
    minLines: Int,
    onLinkTargetClick: OnLinkTargetClick,
) {
    val coroutineScope = rememberCoroutineScope()
    AndroidView(
        modifier = modifier,
        factory = {
            LinkedTextView(it).apply {
                this.gravity = Gravity.START
            }
        },
        update = {
            it.applyUpdate(
                richText = richText,
                finalColor = finalColor,
                fontSp = fontSp,
                coroutineScope = coroutineScope,
                layoutDirection = layoutDirection,
                overflow = overflow,
                maxLines = maxLines,
                minLines = minLines,
                onLinkTargetClick = onLinkTargetClick,
            )
        }
    )
}

@Composable
private fun SelectableTextView(
    modifier: Modifier,
    richText: RichText,
    finalColor: Color,
    fontSp: Float = 14F,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onLinkTargetClick: OnLinkTargetClick,
) {
    val coroutineScope = rememberCoroutineScope()
    AndroidView(
        modifier = modifier,
        factory = {
            LinkedTextView(it).apply {
                this.gravity = Gravity.START
                this.focusable = TextView.FOCUSABLE
                this.isEnabled = true
                this.setTextIsSelectable(true)
                this.isLongClickable = true
            }
        },
        update = {
            it.applyUpdate(
                richText = richText,
                finalColor = finalColor,
                fontSp = fontSp,
                coroutineScope = coroutineScope,
                layoutDirection = layoutDirection,
                overflow = overflow,
                maxLines = maxLines,
                minLines = minLines,
                onLinkTargetClick = onLinkTargetClick,
            )
        }
    )
}

private fun LinkedTextView.applyUpdate(
    richText: RichText,
    finalColor: Color,
    fontSp: Float = 14F,
    coroutineScope: CoroutineScope,
    layoutDirection: LayoutDirection,
    overflow: TextOverflow,
    maxLines: Int,
    minLines: Int,
    onLinkTargetClick: OnLinkTargetClick,
) {
    val textView = this
    textView.textSize = fontSp
    if (finalColor.isSpecified) {
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
    textView.onLinkSpanClick = {
        onLinkTargetClick(context, it.linkTarget)
    }
    val charSequence = richText.parse()
    textView.text = charSequence
    startLoadEmojiImage(
        coroutineScope = coroutineScope,
        context = context,
        textView = textView,
        charSequence = charSequence,
    )
}

private fun startLoadEmojiImage(
    coroutineScope: CoroutineScope,
    context: Context,
    textView: TextView,
    charSequence: CharSequence,
) {
    val customEmojiSpans = charSequence.getAllSpans<CustomEmojiSpan>()
    val pendingLoadSpans = customEmojiSpans.filter {
        it.needLoad
    }
    if (pendingLoadSpans.isEmpty()) return
    coroutineScope.launch {
        pendingLoadSpans.map {
            async {
                it.loadDrawable(context)
                textView.invalidate()
            }
        }.awaitAll()
    }
}

private inline fun <reified T> CharSequence.getAllSpans(): List<T> {
    val spans = (this as? SpannableStringBuilder) ?: return emptyList()
    return spans.getSpans(0, spans.length, T::class.java).toList()
}
