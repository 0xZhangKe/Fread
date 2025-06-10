package com.zhangke.fread.status.ui.richtext

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.seiko.imageloader.rememberImagePainter
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.fread.status.richtext.model.RichLinkTarget

@Composable
fun FreadRichText(
    content: String,
    modifier: Modifier = Modifier,
    mentions: List<Mention> = emptyList(),
    emojis: List<Emoji> = emptyList(),
    tags: List<HashtagInStatus> = emptyList(),
    onMentionClick: (Mention) -> Unit = {},
    onHashtagClick: (HashtagInStatus) -> Unit = {},
    onUrlClick: (url: String) -> Unit = {},
    // layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    fontSizeSp: Float = 14F,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
) {
    val richText = remember(content, mentions) {
        buildRichText(
            document = content,
            mentions = mentions,
            hashTags = tags,
            emojis = emojis,
        )
    }
    FreadRichText(
        modifier = modifier,
        richText = richText,
        // layoutDirection = layoutDirection,
        overflow = overflow,
        maxLines = maxLines,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        onMentionClick = onMentionClick,
        onHashtagClick = onHashtagClick,
        fontSizeSp = fontSizeSp,
        onUrlClick = onUrlClick,
    )
}

@Composable
fun FreadRichText(
    modifier: Modifier,
    richText: RichText,
    color: Color = Color.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    lineHeight: TextUnit = 1.5.em,
    onMentionClick: (Mention) -> Unit = {},
    onMentionDidClick: (String) -> Unit = {},
    onHashtagClick: (HashtagInStatus) -> Unit = {},
    onMaybeHashtagClick: (String) -> Unit = {},
    onUrlClick: (url: String) -> Unit = {},
    // layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    fontSizeSp: Float = 14F,
) {
    DisposableEffect(richText) {
        richText.onLinkTargetClick = { target ->
            when (target) {
                is RichLinkTarget.UrlTarget -> onUrlClick(target.url)
                is RichLinkTarget.MentionTarget -> onMentionClick(target.mention)
                is RichLinkTarget.MentionDidTarget -> onMentionDidClick(target.did)
                is RichLinkTarget.HashtagTarget -> onHashtagClick(target.hashtag)
                is RichLinkTarget.MaybeHashtagTarget -> onMaybeHashtagClick(target.hashtag)
            }
        }
        onDispose {
            richText.onLinkTargetClick = null
        }
    }

    Text(
        text = richText.parse(),
        modifier = modifier,
        color = color,
        overflow = overflow,
        maxLines = maxLines,
        fontStyle = fontStyle,
        lineHeight = lineHeight,
        fontWeight = fontWeight,
        fontSize = fontSizeSp.sp,
        inlineContent = rememberInlineContent(richText.emojis),
    )
}

@Composable
fun SelectableRichText(
    modifier: Modifier,
    richText: RichText,
    color: Color = Color.Unspecified,
    onMentionClick: (Mention) -> Unit = {},
    onHashtagClick: (HashtagInStatus) -> Unit = {},
    onMaybeHashtagClick: (String) -> Unit = {},
    onUrlClick: (url: String) -> Unit = {},
    // layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    fontSizeSp: Float = 14F,
) {
    Box(modifier = modifier) {
        SelectionContainer {
            FreadRichText(
                modifier = Modifier,
                richText = richText,
                color = color,
                onMentionClick = onMentionClick,
                onHashtagClick = onHashtagClick,
                onMaybeHashtagClick = onMaybeHashtagClick,
                onUrlClick = onUrlClick,
                overflow = overflow,
                maxLines = maxLines,
                fontSizeSp = fontSizeSp,
            )
        }
    }
}

@Composable
fun SelectableRichText(
    content: String,
    modifier: Modifier = Modifier,
    mentions: List<Mention> = emptyList(),
    emojis: List<Emoji> = emptyList(),
    tags: List<HashtagInStatus> = emptyList(),
    onMentionClick: (Mention) -> Unit = {},
    onHashtagClick: (HashtagInStatus) -> Unit = {},
    onUrlClick: (url: String) -> Unit = {},
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    fontSizeSp: Float = 14F,
) {
    Box(modifier = modifier) {
        SelectionContainer {
            FreadRichText(
                content = content,
                mentions = mentions,
                emojis = emojis,
                tags = tags,
                onMentionClick = onMentionClick,
                onHashtagClick = onHashtagClick,
                onUrlClick = onUrlClick,
                overflow = overflow,
                maxLines = maxLines,
                fontSizeSp = fontSizeSp,
            )
        }
    }
}

@Composable
private fun rememberInlineContent(
    emojis: List<Emoji>,
): Map<String, InlineTextContent> {
    return remember(emojis) {
        val emojiContent = InlineTextContent(
            placeholder = Placeholder(
                width = 1.5.em,
                height = 1.5.em,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom,
            ),
        ) { shortCode ->
            val fixedShortCode = shortCode.removePrefix(":").removeSuffix(":")
            val emoji = emojis.firstOrNull { it.shortcode == fixedShortCode }
            if (emoji != null) {
                Image(
                    painter = rememberImagePainter(emoji.url),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Text(fixedShortCode)
            }
        }
        mapOf("emoji" to emojiContent)
    }
}
