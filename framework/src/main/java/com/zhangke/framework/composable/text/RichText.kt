package com.zhangke.framework.composable.text

import android.view.Gravity
import android.widget.TextView
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.dmytroshuba.dailytags.core.simple.SimpleMarkupParser
import com.dmytroshuba.dailytags.core.simple.render
import com.dmytroshuba.dailytags.markdown.rules.HtmlRules
import com.dmytroshuba.dailytags.markdown.rules.MarkdownRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RichText(
    modifier: Modifier,
    text: String,
    color: Color = Color.Unspecified,
    fontSp: Float = 14F,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
//    var renderedText: AnnotatedString by remember {
//        mutableStateOf(AnnotatedString(""))
//    }
//    LaunchedEffect(text) {
//        withContext(Dispatchers.IO) {
//            renderedText = try {
//                val rules = MarkdownRules.toList() + HtmlRules.toList()
//                val parser = SimpleMarkupParser()
//                parser.parse(text, rules)
//                    .render()
//                    .toAnnotatedString()
//            } catch (e: Throwable) {
//                AnnotatedString(text)
//            }
//        }
//    }
//    Text(text = renderedText)
    AndroidView(
        modifier = modifier,
        factory = {
            TextView(it)
        },
        update = { textView ->
            textView.textSize = fontSp
            if (color != Color.Unspecified) {
                textView.setTextColor(color.value.toInt())
            }
            textView.maxLines = maxLines
            textView.minLines = minLines
            textView.gravity = Gravity.START
            textView.text = HtmlCompat.fromHtml(
                text,
                HtmlCompat.FROM_HTML_MODE_COMPACT,
            )
        }
    )
}
