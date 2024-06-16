package com.zhangke.utopia.status.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.utils.toPx
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.model.isRss
import com.zhangke.utopia.status.richtext.RichText
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.media.BlogMedias
import com.zhangke.utopia.status.ui.poll.BlogPoll
import com.zhangke.utopia.status.ui.richtext.UtopiaRichText
import com.zhangke.utopia.status.ui.style.BlogStyle

/**
 * 博客正文部分，仅包含内容，投票，媒体。
 */
@Composable
fun BlogContent(
    modifier: Modifier,
    blog: Blog,
    style: BlogStyle,
    indexOfFeeds: Int,
    onMediaClick: OnBlogMediaClick,
    onVoted: (List<BlogPoll.Option>) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onMentionClick: (Mention) -> Unit,
    textSelectable: Boolean = false,
) {
    Column(
        modifier = modifier,
    ) {
        BlogTextContentSection(
            blog = blog,
            style = style,
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
            textSelectable = textSelectable,
        )
        val sensitive = blog.sensitive
        if (blog.mediaList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            BlogMedias(
                modifier = Modifier
                    .fillMaxWidth(),
                mediaList = blog.mediaList,
                indexInList = indexOfFeeds,
                sensitive = sensitive,
                onMediaClick = onMediaClick,
            )
        }
        if (blog.poll != null) {
            Spacer(modifier = Modifier.height(8.dp))
            BlogPoll(
                modifier = Modifier.fillMaxWidth(),
                poll = blog.poll!!,
                onVoted = onVoted,
            )
        }
    }
}

@Composable
private fun BlogTextContentSection(
    blog: Blog,
    style: BlogStyle,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onMentionClick: (Mention) -> Unit,
    textSelectable: Boolean = false,
) {
    val contentMaxLine = if (blog.platform.protocol.isRss) {
        style.contentMaxLine
    } else {
        Int.MAX_VALUE
    }
    val spoilerText = blog.spoilerText
    if (spoilerText.isNotEmpty()) {
        var hideContent by rememberSaveable(spoilerText) {
            mutableStateOf(true)
        }
        SpoilerText(
            hideContent = hideContent,
            spoilerText = RichText(spoilerText, emptyList(), emptyList(), emptyList()),
            onShowContent = { hideContent = false },
            onHideContent = { hideContent = true },
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
        )
        if (blog.content.isNotEmpty()) {
            AnimatedVisibility(
                visible = !hideContent,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                UtopiaRichText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 4.dp),
                    richText = blog.humanizedContent,
                    maxLines = contentMaxLine,
                    onMentionClick = onMentionClick,
                    onHashtagClick = onHashtagInStatusClick,
                    textSelectable = textSelectable,
                )
            }
        }
    } else {
        if (!blog.title.isNullOrEmpty()) {
            Text(
                modifier = Modifier
                    .padding(top = 8.dp),
                text = blog.title!!,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
        if (!blog.description.isNullOrEmpty()) {
            val topPadding = if (blog.title.isNullOrEmpty()) {
                8.dp
            } else {
                4.dp
            }
            UtopiaRichText(
                modifier = Modifier
                    .padding(top = topPadding),
                richText = blog.humanizedDescription,
                maxLines = contentMaxLine,
                onMentionClick = onMentionClick,
                onHashtagClick = onHashtagInStatusClick,
                textSelectable = textSelectable,
            )
        }
        if (
            blog.title.isNullOrEmpty() &&
            blog.description.isNullOrEmpty() &&
            blog.content.isNotEmpty()
        ) {
            UtopiaRichText(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 8.dp),
                richText = blog.humanizedContent,
                maxLines = contentMaxLine,
                onMentionClick = onMentionClick,
                onHashtagClick = onHashtagInStatusClick,
                textSelectable = textSelectable,
            )
        }
    }
}

@Composable
private fun SpoilerText(
    hideContent: Boolean,
    spoilerText: RichText,
    textSelectable: Boolean = false,
    onShowContent: () -> Unit,
    onHideContent: () -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onMentionClick: (Mention) -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .drawSpoilerBackground()
            .noRippleClick {
                if (hideContent) {
                    onShowContent()
                } else {
                    onHideContent()
                }
            }
    ) {
        UtopiaRichText(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 6.dp),
            richText = spoilerText,
            onMentionClick = onMentionClick,
            onHashtagClick = onHashtagInStatusClick,
            textSelectable = textSelectable,
        )
    }
}

@Composable
fun Modifier.drawSpoilerBackground(): Modifier {
    val edgeColor = Color(0xFFFFB84D)
    val backgroundColor = Color(0xFFFFEED3)
    val edgeWidth = 8.dp.toPx()
    val cornerRadiiPx = 6.dp.toPx()
    val cornerRadius = CornerRadius(cornerRadiiPx, cornerRadiiPx)
    return this.drawBehind {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val startEdge = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = Offset.Zero,
                        Size(width = edgeWidth, height = canvasHeight),
                    ),
                    topLeft = cornerRadius,
                    bottomLeft = cornerRadius,
                )
            )
        }
        val endEdge = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = Offset(x = canvasWidth - edgeWidth, y = 0F),
                        Size(width = edgeWidth, height = canvasHeight),
                    ),
                    topRight = cornerRadius,
                    bottomRight = cornerRadius,
                )
            )
        }
        drawPath(startEdge, edgeColor)
        drawPath(endEdge, edgeColor)
        drawRect(
            color = backgroundColor,
            topLeft = Offset(x = edgeWidth, y = 0F),
            size = size.copy(width = canvasWidth - edgeWidth * 2),
        )
        var pointStartOffset = 18.dp.toPx()
        val pointRadii = 1.5.dp.toPx()
        repeat(3) {
            drawCircle(
                color = Color.Black.copy(alpha = 0.8F),
                radius = pointRadii,
                center = Offset(x = pointStartOffset, y = 14.dp.toPx())
            )
            pointStartOffset += pointRadii + 6.dp.toPx()
        }
    }
}
