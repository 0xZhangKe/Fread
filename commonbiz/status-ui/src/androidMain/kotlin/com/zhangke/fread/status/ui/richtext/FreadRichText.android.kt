package com.zhangke.fread.status.ui.richtext

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.model.RichLinkTarget
import com.zhangke.fread.status.ui.richtext.android.AndroidRichText

@Composable
actual fun FreadRichText(
    modifier: Modifier,
    richText: RichText,
    color: Color,
    onMentionClick: (Mention) -> Unit,
    onHashtagClick: (HashtagInStatus) -> Unit,
    onMaybeHashtagTarget: (RichLinkTarget.MaybeHashtagTarget) -> Unit,
    onUrlClick: (url: String) -> Unit,
    layoutDirection: LayoutDirection,
    overflow: TextOverflow,
    maxLines: Int,
    textSelectable: Boolean,
    fontSizeSp: Float,
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
                is RichLinkTarget.UrlTarget -> {
                    onUrlClick(linkTarget.url)
                }

                is RichLinkTarget.HashtagTarget -> {
                    onHashtagClick(linkTarget.hashtag)
                }

                is RichLinkTarget.MentionTarget -> {
                    onMentionClick(linkTarget.mention)
                }

                is RichLinkTarget.MaybeHashtagTarget -> {
                    onMaybeHashtagTarget.invoke(linkTarget)
                }
            }
        },
    )
}
