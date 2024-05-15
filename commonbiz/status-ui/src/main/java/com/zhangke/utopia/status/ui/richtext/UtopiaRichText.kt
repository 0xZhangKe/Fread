package com.zhangke.utopia.status.ui.richtext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import com.zhangke.framework.browser.BrowserLauncher
import com.zhangke.utopia.status.model.Emoji
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.richtext.RichText
import com.zhangke.utopia.status.richtext.android.span.LinkSpan
import com.zhangke.utopia.status.richtext.buildRichText
import com.zhangke.utopia.status.ui.richtext.android.AndroidRichText

@Composable
fun UtopiaRichText(
    modifier: Modifier,
    richText: RichText,
    onMentionClick: (Mention) -> Unit,
    onHashtagClick: (HashtagInStatus) -> Unit,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    textSelectable: Boolean = false,
) {
    AndroidRichText(
        modifier = modifier,
        richText = richText,
        layoutDirection = layoutDirection,
        overflow = overflow,
        maxLines = maxLines,
        onLinkTargetClick = { context, linkTarget ->
            when (linkTarget) {
                is LinkSpan.LinkTarget.UrlTarget -> {
                    BrowserLauncher().launch(context, linkTarget.url)
                }

                is LinkSpan.LinkTarget.HashtagTarget -> {
                    onHashtagClick(linkTarget.hashtag)
                }

                is LinkSpan.LinkTarget.MentionTarget -> {
                    onMentionClick(linkTarget.mention)
                }
            }
        },
    )
}

@Composable
fun UtopiaRichText(
    modifier: Modifier,
    content: String,
    mentions: List<Mention>,
    emojis: List<Emoji>,
    tags: List<HashtagInStatus>,
    onMentionClick: (Mention) -> Unit,
    onHashtagClick: (HashtagInStatus) -> Unit,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
) {
    val richText = remember(content, mentions) {
        buildRichText(
            document = content,
            mentions = mentions,
            hashTags = tags,
            emojis = emojis,
        )
    }
    UtopiaRichText(
        modifier = modifier,
        richText = richText,
        layoutDirection = layoutDirection,
        overflow = overflow,
        maxLines = maxLines,
        onMentionClick = onMentionClick,
        onHashtagClick = onHashtagClick,
    )
}
