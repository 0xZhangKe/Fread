package com.zhangke.fread.status.ui.embed

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.status.blog.BlogEmbed
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
internal fun BlogEmbedUi(
    modifier: Modifier,
    embed: BlogEmbed,
    style: StatusStyle,
    onClick: (BlogEmbed) -> Unit,
) {
    when (embed) {
        is BlogEmbed.Link -> {
            StatusEmbedLinkUi(
                modifier = modifier
                    .padding(top = style.contentStyle.contentVerticalSpacing)
                    .fillMaxWidth(),
                linkEmbed = embed,
                style = style,
                onCardClick = onClick,
            )
        }

        is BlogEmbed.Blog -> {

        }
    }
}
