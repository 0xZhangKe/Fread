package com.zhangke.utopia.status.ui.richtext

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Html
import android.text.style.ImageSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import coil.Coil
import coil.executeBlocking
import coil.request.ImageRequest
import com.zhangke.framework.utils.DrawableWrapper
import com.zhangke.utopia.status.model.Emoji
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xml.sax.XMLReader

@Composable
fun RichText(
    modifier: Modifier,
    text: String,
    color: Color = Color.Unspecified,
    emojis: List<Emoji>,
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
    val coroutineScope = rememberCoroutineScope()
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
            textView.text = parseHtml(
                context = textView.context,
                coroutineScope = coroutineScope,
                html = text,
                emojis = emojis,
                view = textView,
            )
        }
    )
}

private fun parseHtml(
    context: Context,
    coroutineScope: CoroutineScope,
    view: View,
    html: String,
    emojis: List<Emoji>,
): CharSequence {
    var fixedHtml = html
    emojis.forEach { emoji ->
        fixedHtml =
            fixedHtml.replace(
                ":${emoji.shortcode}:",
                "<img src=\"${emoji.url}\" alt=\"${emoji.shortcode}\" />",
            )
    }
    return HtmlCompat.fromHtml(
        fixedHtml,
        HtmlCompat.FROM_HTML_MODE_COMPACT,
        HtmlImageGetter(
            view = view,
            context = context,
            coroutineScope = coroutineScope,
        ),
        object : Html.TagHandler {
            override fun handleTag(
                opening: Boolean,
                tag: String,
                output: Editable,
                xmlReader: XMLReader,
            ) {
                Log.d("U_TEST", "handleTag: $opening, $tag}")
            }
        },
    ).trim().also {
        Log.d("U_TEST", "parseHtml: $it")
    }
}

//class AsyncImageSpan: ImageSpan(){
//
//}

private class HtmlImageGetter(
    private val view: View,
    private val context: Context,
    private val coroutineScope: CoroutineScope,
) : Html.ImageGetter {

    override fun getDrawable(source: String?): Drawable? {
        source ?: return null

        return AsyncImageDrawable(
            view = view,
            url = source,
            context = context,
            coroutineScope = coroutineScope,
        )
    }
}

class AsyncImageDrawable(
    private val view: View,
    private val url: String,
    private val context: Context,
    coroutineScope: CoroutineScope,
) : DrawableWrapper() {

    init {
        coroutineScope.launch {
            val drawable = withContext(Dispatchers.IO) {
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .build()
                Coil.imageLoader(context).executeBlocking(request).drawable
            }

            Log.d(
                "U_TEST",
                "url: $url, callback: $callback, ${drawable?.intrinsicWidth}: ${drawable?.intrinsicHeight}"
            )
            setWrappedDrawable(drawable)
            view.postDelayed({
                invalidateSelf()
            }, 1000)
        }
    }

    override fun draw(canvas: Canvas) {
        Log.d("U_TEST", "draw: ${getWrappedDrawable()}")
        super.draw(canvas)
    }
}
