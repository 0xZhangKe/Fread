package com.zhangke.fread.status.ui.embed

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogEmbed
import com.zhangke.fread.status.model.Facet
import com.zhangke.fread.status.model.FacetFeatureUnion
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
internal fun BlogEmbedsUi(
    modifier: Modifier,
    embeds: List<BlogEmbed>,
    blog: Blog,
    style: StatusStyle,
    onContentClick: (Blog) -> Unit,
    onUrlClick: (url: String) -> Unit,
    onUnavailableQuoteClick: (String) -> Unit,
) {
    if (embeds.isEmpty()) return
    embeds.forEach { embed ->
        BlogEmbedUi(
            modifier = modifier
                .padding(top = style.contentStyle.contentVerticalSpacing),
            embed = embed,
            blog = blog,
            style = style,
            onUrlClick = onUrlClick,
            onContentClick = onContentClick,
            onUnavailableQuoteClick = onUnavailableQuoteClick,
        )
    }
}

@Composable
private fun BlogEmbedUi(
    modifier: Modifier,
    embed: BlogEmbed,
    blog: Blog,
    style: StatusStyle,
    onContentClick: (Blog) -> Unit,
    onUrlClick: (url: String) -> Unit,
    onUnavailableQuoteClick: (String) -> Unit,
) {
    when (embed) {
        is BlogEmbed.Link -> {
            if (embed.isGif) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .aspectRatio(embed.aspectRatio)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onUrlClick(embed.url) },
                ) {
                    AutoSizeImage(
                        request = remember(embed.url) { ImageRequest(embed.url) },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        contentDescription = embed.title.ifEmpty { "GIF" },
                    )
                }
            } else {
                StatusEmbedLinkUi(
                    modifier = modifier
                        .embedBorder()
                        .fillMaxWidth(),
                    linkEmbed = embed,
                    style = style.cardStyle,
                    onCardClick = { onUrlClick(embed.url) },
                    showDomain = !embed.appearsInText(blog.content, blog.facets),
                )
            }
        }

        is BlogEmbed.Blog -> {
            BlogInEmbedding(
                modifier = modifier
                    .embedBorder()
                    .padding(8.dp),
                blog = embed.blog,
                style = style,
                onContentClick = onContentClick,
            )
        }

        is BlogEmbed.UnavailableQuote -> {
            UnavailableQuoteInEmbedding(
                modifier = modifier.embedBorder().padding(16.dp),
                unavailableQuote = embed,
                onContentClick = onUnavailableQuoteClick,
            )
        }
    }
}

/**
 * True when the user's post text references this card's URL — either as a
 * literal substring (`https://example.com/foo` typed inline) or via a rich
 * facet whose `uri` matches. False when Bluesky kept the link card but the
 * URL was deleted from the text; that's the case the domain label exists to
 * disambiguate.
 */
internal fun BlogEmbed.Link.appearsInText(content: String, facets: List<Facet>): Boolean {
    if (content.contains(url)) return true
    return facets.any { facet ->
        facet.features.any { it is FacetFeatureUnion.Link && it.uri == url }
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
