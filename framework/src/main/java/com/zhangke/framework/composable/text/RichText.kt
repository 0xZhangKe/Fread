package com.zhangke.framework.composable.text

import android.view.Gravity
import android.widget.TextView
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Composable
fun RichText(
    modifier: Modifier,
    text: String,
    color: Color = Color.Unspecified,
    fontSp: Float = 14F,
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
    AndroidView(
        modifier = modifier,
        factory = {
            TextView(it)
        },
        update = { textView ->
            textView.textSize = fontSp
            if (color != Color.Unspecified) {
                textView.setTextColor(finalColor.value.toInt())
            }
            textView.maxLines = maxLines
            textView.minLines = minLines
            textView.gravity = Gravity.START
            textView.text = HtmlCompat.fromHtml(
                text,
                HtmlCompat.FROM_HTML_MODE_COMPACT,
            ).trim()
        }
    )
}
