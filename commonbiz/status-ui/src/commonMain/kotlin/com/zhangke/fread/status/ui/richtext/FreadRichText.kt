package com.zhangke.fread.status.ui.richtext

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.style.TextOverflow
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
    textSelectable: Boolean = false,
    fontSizeSp: Float = 14F,
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
        onMentionClick = onMentionClick,
        onHashtagClick = onHashtagClick,
        fontSizeSp = fontSizeSp,
        onUrlClick = onUrlClick,
        textSelectable = textSelectable,
    )
}

@Composable
fun FreadRichText(
    modifier: Modifier,
    richText: RichText,
    color: Color = Color.Unspecified,
    onMentionClick: (Mention) -> Unit = {},
    onHashtagClick: (HashtagInStatus) -> Unit = {},
    onMaybeHashtagTarget: (RichLinkTarget.MaybeHashtagTarget) -> Unit = {},
    onUrlClick: (url: String) -> Unit = {},
    // layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    textSelectable: Boolean = false,
    fontSizeSp: Float = 14F,
) {
    DisposableEffect(richText) {
        richText.onLinkTargetClick = { target ->
            when (target) {
                is RichLinkTarget.UrlTarget -> onUrlClick(target.url)
                is RichLinkTarget.MentionTarget -> onMentionClick(target.mention)
                is RichLinkTarget.HashtagTarget -> onHashtagClick(target.hashtag)
                is RichLinkTarget.MaybeHashtagTarget -> onMaybeHashtagTarget(target)
            }
        }
        onDispose {
            richText.onLinkTargetClick = null
        }
    }

    TextSelectionContainer(textSelectable) {
        Text(
            text = richText.parseRichText,
            modifier = modifier,
            color = color,
            overflow = overflow,
            maxLines = maxLines,
            fontSize = fontSizeSp.sp,
            inlineContent = customInlineContent,
        )
    }
}

@Composable
private fun TextSelectionContainer(
    enabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    if (enabled) {
        SelectionContainer(content = content)
    } else {
        content()
    }
}

private val customInlineContent by lazy(LazyThreadSafetyMode.NONE) {
    mapOf(
        "emoji" to InlineTextContent(
            Placeholder(
                width = 1.em,
                height = 1.em,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom,
            ),
        ) { emojiUrl ->
            Image(
                rememberImagePainter(emojiUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
    )
}