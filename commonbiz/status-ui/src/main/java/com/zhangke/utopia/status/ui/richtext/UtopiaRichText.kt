package com.zhangke.utopia.status.ui.richtext

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import com.zhangke.framework.composable.richtext.RichText
import com.zhangke.framework.composable.richtext.RichTextUi
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.richtext.buildRichText

@Composable
fun UtopiaRichText(
    modifier: Modifier,
    richText: RichText,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    RichTextUi(
        modifier = modifier,
        richText = richText,
        layoutDirection = layoutDirection,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        textStyle = textStyle,
    )
}

@Composable
fun UtopiaRichText(
    modifier: Modifier,
    content: String,
    mentions: List<Mention>,
    baseUrl: FormalBaseUrl? = null,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    val richText = remember(content, mentions, baseUrl) {
        buildRichText(content, mentions, baseUrl)
    }
    RichTextUi(
        modifier = modifier,
        richText = richText,
        layoutDirection = layoutDirection,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        textStyle = textStyle,
    )
}
