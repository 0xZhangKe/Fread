package com.zhangke.utopia.status.ui.richtext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.richtext.RichText
import com.zhangke.utopia.status.richtext.buildRichText
import com.zhangke.utopia.status.ui.richtext.android.AndroidRichText

@Composable
fun UtopiaRichText(
    modifier: Modifier,
    richText: RichText,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    AndroidRichText(
        modifier = modifier,
        richText = richText,
        layoutDirection = layoutDirection,
        overflow = overflow,
        maxLines = maxLines,
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
    maxLines: Int = Int.MAX_VALUE,
) {
    val richText = remember(content, mentions, baseUrl) {
        buildRichText(
            document = content,
            mentions = mentions,
            baseUrl = baseUrl,
            hashTags = emptyList(),
            emojis = emptyList(),
        )
    }
    UtopiaRichText(
        modifier = modifier,
        richText = richText,
        layoutDirection = layoutDirection,
        overflow = overflow,
        maxLines = maxLines,
    )
}
