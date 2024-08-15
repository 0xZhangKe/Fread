package com.zhangke.fread.status.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.zhangke.framework.architect.theme.inverseOnSurfaceDark
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.utils.toPx
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.isRss
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.common.BlogTranslationState
import com.zhangke.fread.status.ui.image.BlogMediaClickEvent
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.label.StatusBottomEditedLabel
import com.zhangke.fread.status.ui.label.StatusBottomInteractionLabel
import com.zhangke.fread.status.ui.label.StatusBottomTimeLabel
import com.zhangke.fread.status.ui.media.BlogMedias
import com.zhangke.fread.status.ui.poll.BlogPoll
import com.zhangke.fread.status.ui.preview.StatusPreviewCardUi
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.StatusStyle

/**
 * 博客正文部分，仅包含内容，投票，媒体，链接预览卡片。
 */
@Composable
fun BlogContent(
    modifier: Modifier,
    blog: Blog,
    blogTranslationState: BlogTranslationUiState?,
    specificTime: String,
    style: StatusStyle,
    indexOfFeeds: Int,
    onMediaClick: OnBlogMediaClick,
    onVoted: (List<BlogPoll.Option>) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onBoostedClick: ((String) -> Unit)? = null,
    onFavouritedClick: ((String) -> Unit)? = null,
    onUrlClick: (url: String) -> Unit,
    onMentionClick: (Mention) -> Unit,
    textSelectable: Boolean = false,
    boostedCount: Int? = null,
    favouritedCount: Int? = null,
    detailModel: Boolean = false,
    editedTime: String? = null,
) {
    Column(
        modifier = modifier,
    ) {
        if (blogTranslationState != null) {
            BlogTranslationState(
                modifier = Modifier,
                translationUiState = blogTranslationState,
                style = style,
            )
        }
        BlogTextContentSection(
            blog = blog,
            style = style,
            onHashtagInStatusClick = {
                reportClick(StatusDataElements.HASHTAG)
                onHashtagInStatusClick(it)
            },
            onMentionClick = {
                reportClick(StatusDataElements.MENTION)
                onMentionClick(it)
            },
            textSelectable = textSelectable,
            onUrlClick = onUrlClick,
        )
        val sensitive = blog.sensitive
        if (blog.poll != null) {
            BlogPoll(
                modifier = Modifier
                    .padding(top = style.contentStyle.contentVerticalSpacing)
                    .fillMaxWidth(),
                poll = blog.poll!!,
                onVoted = {
                    reportClick(StatusDataElements.VOTE)
                    onVoted(it)
                },
            )
        } else if (blog.mediaList.isNotEmpty()) {
            BlogMedias(
                modifier = Modifier
                    .padding(top = style.contentStyle.contentVerticalSpacing)
                    .fillMaxWidth(),
                mediaList = blog.mediaList,
                indexInList = indexOfFeeds,
                sensitive = sensitive,
                onMediaClick = {
                    reportClick(StatusDataElements.MEDIA) {
                        val mediaType = when (it) {
                            is BlogMediaClickEvent.BlogImageClickEvent -> "image"
                            is BlogMediaClickEvent.BlogVideoClickEvent -> "video"
                        }
                        put("mediaType", mediaType)
                    }
                    onMediaClick(it)
                },
            )
        } else if (blog.card != null) {
            StatusPreviewCardUi(
                modifier = Modifier
                    .padding(top = style.contentStyle.contentVerticalSpacing)
                    .fillMaxWidth(),
                card = blog.card!!,
                style = style,
                onCardClick = {
                    onUrlClick(it.url)
                },
            )
        }

        if (detailModel) {
            StatusBottomTimeLabel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = style.contentStyle.contentVerticalSpacing),
                blog = blog,
                specificTime = specificTime,
                style = style,
                onUrlClick = onUrlClick,
            )
            if (!editedTime.isNullOrEmpty()) {
                StatusBottomEditedLabel(
                    modifier = Modifier
                        .padding(top = style.contentStyle.contentVerticalSpacing),
                    editedAt = editedTime,
                    style = style,
                )
            }
            if (favouritedCount != null && boostedCount != null) {
                StatusBottomInteractionLabel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = style.contentStyle.contentVerticalSpacing),
                    boostedCount = boostedCount,
                    favouritedCount = favouritedCount,
                    style = style,
                    onBoostedClick = { onBoostedClick?.invoke(blog.id) },
                    onFavouritedClick = { onFavouritedClick?.invoke(blog.id) },
                )
            }
        }
    }
}

@Composable
private fun BlogTextContentSection(
    blog: Blog,
    style: StatusStyle,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onMentionClick: (Mention) -> Unit,
    onUrlClick: (url: String) -> Unit,
    textSelectable: Boolean = false,
) {
    val contentMaxLine = if (blog.platform.protocol.isRss) {
        style.contentStyle.maxLine
    } else {
        Int.MAX_VALUE
    }
    val spoilerText = blog.spoilerText
    if (spoilerText.isNotEmpty()) {
        var hideContent by rememberSaveable(spoilerText) {
            mutableStateOf(true)
        }
        SpoilerText(
            modifier = Modifier,
            hideContent = hideContent,
            spoilerText = blog.humanizedSpoilerText,
            fontSize = style.contentStyle.contentSize,
            onShowContent = { hideContent = false },
            onHideContent = { hideContent = true },
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
            onUrlClick = onUrlClick,
        )
        if (blog.content.isNotEmpty()) {
            AnimatedVisibility(
                visible = !hideContent,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                FreadRichText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .wrapContentHeight(),
                    richText = blog.humanizedContent,
                    maxLines = contentMaxLine,
                    onMentionClick = onMentionClick,
                    onHashtagClick = onHashtagInStatusClick,
                    textSelectable = textSelectable,
                    onUrlClick = onUrlClick,
                    fontSizeSp = style.contentStyle.contentSize.value,
                )
            }
        }
    } else {
        if (!blog.title.isNullOrEmpty()) {
            Text(
                modifier = Modifier,
                text = blog.title!!,
                fontWeight = FontWeight.Bold,
                fontSize = style.contentStyle.titleSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        if (!blog.description.isNullOrEmpty()) {
            val topPadding = if (blog.title.isNullOrEmpty()) {
                style.contentStyle.contentVerticalSpacing
            } else {
                style.contentStyle.contentVerticalSpacing / 2
            }
            FreadRichText(
                modifier = Modifier
                    .padding(top = topPadding),
                richText = blog.humanizedDescription,
                maxLines = contentMaxLine,
                onMentionClick = onMentionClick,
                onHashtagClick = onHashtagInStatusClick,
                onUrlClick = onUrlClick,
                fontSizeSp = style.contentStyle.contentSize.value,
                textSelectable = textSelectable,
            )
        }
        if (
            blog.title.isNullOrEmpty() &&
            blog.description.isNullOrEmpty() &&
            blog.content.isNotEmpty()
        ) {
            FreadRichText(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                richText = blog.humanizedContent,
                maxLines = contentMaxLine,
                onMentionClick = onMentionClick,
                onHashtagClick = onHashtagInStatusClick,
                textSelectable = textSelectable,
                fontSizeSp = style.contentStyle.contentSize.value,
                onUrlClick = onUrlClick,
            )
        }
    }
}

@Composable
private fun SpoilerText(
    modifier: Modifier,
    hideContent: Boolean,
    spoilerText: RichText,
    fontSize: TextUnit,
    textSelectable: Boolean = false,
    onShowContent: () -> Unit,
    onHideContent: () -> Unit,
    onUrlClick: (url: String) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onMentionClick: (Mention) -> Unit,
) {
    Box(
        modifier = modifier
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
        FreadRichText(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 6.dp),
            richText = spoilerText,
            color = inverseOnSurfaceDark,
            onMentionClick = onMentionClick,
            onHashtagClick = onHashtagInStatusClick,
            onUrlClick = onUrlClick,
            textSelectable = textSelectable,
            fontSizeSp = fontSize.value,
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
