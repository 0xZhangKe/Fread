package com.zhangke.utopia.status.ui.richtext.android

import android.text.SpannableStringBuilder
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
            coroutineScope.launch {
                val spans = (charSequence as? SpannableStringBuilder) ?: return@launch
                val customEmojiSpans = spans.getSpans(0, spans.length, CustomEmojiSpan::class.java)
                customEmojiSpans.map {
                    async {
                        it.loadDrawable(context)
                        textView.invalidate()
                    }
                }.awaitAll()
            }
        }
    )
}
