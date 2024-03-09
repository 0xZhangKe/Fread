package com.zhangke.framework.composable.richtext

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import moe.tlaster.ktml.Ktml
import moe.tlaster.ktml.dom.Element

class RichText(
    @Suppress("MemberVisibilityCanBePrivate")
    val document: String,
    private val preProcess: (String) -> String = { it },
    private val postProcess: (Element) -> Element = { it },
) {

    private var element: Element? = null

    fun parseElement(): Element {
        element?.let { return it }
        val doc = preProcess(document)
        val ele = Ktml.parse(doc).let(postProcess)
        element = ele
        return ele
    }
}

@Composable
fun RichTextUi(
    modifier: Modifier,
    richText: RichText,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    val element = remember(richText) {
        richText.parseElement()
    }
    HtmlText2(
        modifier = modifier,
        element = element,
        layoutDirection = layoutDirection,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        textStyle = textStyle,
    )
}
