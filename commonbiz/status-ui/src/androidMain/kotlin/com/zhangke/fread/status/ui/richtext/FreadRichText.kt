package com.zhangke.fread.status.ui.richtext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.android.span.LinkSpan
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.fread.status.ui.richtext.android.AndroidRichText

@Composable
fun FreadRichText(
    modifier: Modifier,
    richText: RichText,
    color: Color = Color.Unspecified,
    onMentionClick: (Mention) -> Unit = {},
    onHashtagClick: (HashtagInStatus) -> Unit = {},
    onMaybeHashtagTarget: (LinkSpan.LinkTarget.MaybeHashtagTarget) -> Unit = {},
    onUrlClick: (url: String) -> Unit = {},
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    textSelectable: Boolean = false,
    fontSizeSp: Float = 14F,
) {
    AndroidRichText(
        modifier = modifier,
        richText = richText,
        layoutDirection = layoutDirection,
        overflow = overflow,
        maxLines = maxLines,
        color = color,
        fontSp = fontSizeSp,
        textSelectable = textSelectable,
        onLinkTargetClick = { _, linkTarget ->
            when (linkTarget) {
                is LinkSpan.LinkTarget.UrlTarget -> {
                    onUrlClick(linkTarget.url)
                }

                is LinkSpan.LinkTarget.HashtagTarget -> {
                    onHashtagClick(linkTarget.hashtag)
                }

                is LinkSpan.LinkTarget.MentionTarget -> {
                    onMentionClick(linkTarget.mention)
                }

                is LinkSpan.LinkTarget.MaybeHashtagTarget -> {
                    onMaybeHashtagTarget.invoke(linkTarget)
                }
            }
        },
    )
}

@Composable
fun FreadRichText(
    modifier: Modifier,
    content: String,
    mentions: List<Mention> = emptyList(),
    emojis: List<Emoji> = emptyList(),
    tags: List<HashtagInStatus> = emptyList(),
    onMentionClick: (Mention) -> Unit = {},
    onHashtagClick: (HashtagInStatus) -> Unit = {},
    onUrlClick: (url: String) -> Unit = {},
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
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
        layoutDirection = layoutDirection,
        overflow = overflow,
        maxLines = maxLines,
        onMentionClick = onMentionClick,
        onHashtagClick = onHashtagClick,
        fontSizeSp = fontSizeSp,
        onUrlClick = onUrlClick,
        textSelectable = textSelectable,
    )
}
