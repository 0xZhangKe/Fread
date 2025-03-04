package com.zhangke.fread.status.ui.embed

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogEmbed
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
internal fun BlogEmbedsUi(
    modifier: Modifier,
    embeds: List<BlogEmbed>,
    style: StatusStyle,
    onContentClick: (Blog) -> Unit,
    onUrlClick: (url: String) -> Unit,
) {
    if (embeds.isEmpty()) return
    embeds.forEach { embed ->
        BlogEmbedUi(
            modifier = modifier,
            embed = embed,
            style = style,
            onUrlClick = onUrlClick,
            onContentClick = onContentClick,
        )
    }
}

@Composable
private fun BlogEmbedUi(
    modifier: Modifier,
    embed: BlogEmbed,
    style: StatusStyle,
    onContentClick: (Blog) -> Unit,
    onUrlClick: (url: String) -> Unit,
) {
    when (embed) {
        is BlogEmbed.Link -> {
            StatusEmbedLinkUi(
                modifier = modifier
                    .embedBorder()
                    .padding(top = style.contentStyle.contentVerticalSpacing)
                    .fillMaxWidth(),
                linkEmbed = embed,
                style = style.cardStyle,
                onCardClick = { onUrlClick(embed.url) },
            )
        }

        is BlogEmbed.Blog -> {
            BlogInEmbedding(
                modifier = modifier
                    .padding(8.dp)
                    .embedBorder(),
                blog = embed.blog,
                style = style,
                onContentClick = onContentClick,
            )
        }
    }
}

@Composable
fun Modifier.embedBorder(): Modifier {
    return this.border(
        width = 1.dp,
        color = DividerDefaults.color,
        shape = RoundedCornerShape(8.dp),
    )
}
