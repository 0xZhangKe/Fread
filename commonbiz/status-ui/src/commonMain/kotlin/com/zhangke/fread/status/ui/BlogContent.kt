package com.zhangke.fread.status.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.zhangke.framework.architect.theme.inverseOnSurfaceDark
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.utils.toPx
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.isRss
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.common.BlogTranslateLabel
import com.zhangke.fread.status.ui.embed.BlogEmbedsUi
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.label.StatusBottomEditedLabel
import com.zhangke.fread.status.ui.label.StatusBottomInteractionLabel
import com.zhangke.fread.status.ui.label.StatusBottomTimeLabel
import com.zhangke.fread.status.ui.media.BlogMedias
import com.zhangke.fread.status.ui.poll.BlogPoll
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.style.StatusStyle.ContentStyle

/**
 * 博客正文部分，仅包含内容，投票，媒体，链接预览卡片。
 */
@Composable
fun BlogContent(
    modifier: Modifier,
    blog: Blog,
    isOwner: Boolean?,
    style: StatusStyle,
    indexOfFeeds: Int,
    onBlogClick: (Blog) -> Unit,
    onMediaClick: OnBlogMediaClick = {},
    blogTranslationState: BlogTranslationUiState = BlogTranslationUiState.DEFAULT,
    onVoted: (List<BlogPoll.Option>) -> Unit = {},
    onHashtagInStatusClick: (HashtagInStatus) -> Unit = {},
    onMaybeHashtagClick: (String) -> Unit,
    onBoostedClick: ((String) -> Unit)? = null,
    onFavouritedClick: ((String) -> Unit)? = null,
    onUrlClick: (url: String) -> Unit = {},
    onMentionClick: (Mention) -> Unit = {},
    onMentionDidClick: (String) -> Unit = {},
    onShowOriginalClick: () -> Unit,
    detailModel: Boolean = false,
    editedTime: String? = null,
) {
    Column(
        modifier = modifier,
    ) {
        BlogTranslateLabel(
            modifier = Modifier,
            style = style,
            blogTranslationState = blogTranslationState,
            onShowOriginalClick = onShowOriginalClick,
        )
        if (detailModel) {
            SelectionContainer {
                Column {
                    BlogTextContentSection(
                        blog = blog,
                        blogTranslationState = blogTranslationState,
                        style = style.contentStyle,
                        onHashtagInStatusClick = onHashtagInStatusClick,
                        onMentionClick = onMentionClick,
                        onMentionDidClick = onMentionDidClick,
                        onMaybeHashtagClick = onMaybeHashtagClick,
                        onUrlClick = onUrlClick,
                    )
                }
            }
        } else {
            BlogTextContentSection(
                blog = blog,
                blogTranslationState = blogTranslationState,
                style = style.contentStyle,
                onHashtagInStatusClick = onHashtagInStatusClick,
                onMentionClick = onMentionClick,
                onMentionDidClick = onMentionDidClick,
                onUrlClick = onUrlClick,
                onMaybeHashtagClick = onMaybeHashtagClick,
            )
        }
        val sensitive = blog.sensitive || blog.sensitiveByFilter
        if (blog.poll != null) {
            BlogPoll(
                modifier = Modifier
                    .padding(top = style.contentStyle.contentVerticalSpacing)
                    .fillMaxWidth(),
                poll = blog.poll!!,
                isSelf = isOwner,
                blogTranslationState = blogTranslationState,
                onVoted = {
                    onVoted(it)
                },
            )
        } else if (blog.mediaList.isNotEmpty()) {
            BlogMedias(
                modifier = Modifier
                    .padding(top = style.contentStyle.contentVerticalSpacing)
                    .fillMaxWidth(),
                mediaList = blog.mediaList,
                blogTranslationState = blogTranslationState,
                indexInList = indexOfFeeds,
                sensitive = sensitive,
                onMediaClick = onMediaClick,
            )
        } else if (blog.embeds.isNotEmpty()) {
            BlogEmbedsUi(
                modifier = Modifier
                    .padding(top = style.contentStyle.contentVerticalSpacing)
                    .fillMaxWidth(),
                embeds = blog.embeds,
                style = style,
                onContentClick = onBlogClick,
                onUrlClick = onUrlClick,
            )
        }

        if (detailModel) {
            StatusBottomTimeLabel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = style.contentStyle.contentVerticalSpacing),
                blog = blog,
                specificTime = blog.formattedCreateAt,
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
            if (blog.like.likedCount != null && blog.forward.forwardCount != null) {
                StatusBottomInteractionLabel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = style.contentStyle.contentVerticalSpacing),
                    boostedCount = blog.forward.forwardCount!!,
                    favouritedCount = blog.like.likedCount!!,
                    style = style,
                    onBoostedClick = { onBoostedClick?.invoke(blog.id) },
                    onFavouritedClick = { onFavouritedClick?.invoke(blog.id) },
                )
            }
        }
    }
}

@Composable
fun BlogTextContentSection(
    blog: Blog,
    style: ContentStyle,
    blogTranslationState: BlogTranslationUiState? = null,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit = {},
    onMaybeHashtagClick: (String) -> Unit = {},
    onMentionClick: (Mention) -> Unit = {},
    onMentionDidClick: (String) -> Unit = {},
    onUrlClick: (url: String) -> Unit = {},
) {
    val contentMaxLine: Int = if (blog.platform.protocol.isRss) {
        style.maxLine
    } else {
        Int.MAX_VALUE
    }
    val showWarning = blog.spoilerText.isNotEmpty() || blog.sensitiveByFilter
    val spoilerText = blog.spoilerText
    if (showWarning) {
        val statusConfig = LocalStatusUiConfig.current
        var hideContent by rememberSaveable(
            showWarning,
            spoilerText,
            statusConfig.alwaysShowSensitiveContent,
        ) {
            mutableStateOf(!statusConfig.alwaysShowSensitiveContent)
        }
        val humanizedSpoilerText = if (blogTranslationState?.showingTranslation == true) {
            blogTranslationState.blogTranslation!!.getHumanizedSpoilerText(blog)
        } else if (blog.spoilerText.isNotEmpty()) {
            blog.humanizedSpoilerText
        } else {
            val text =
                org.jetbrains.compose.resources.stringResource(
                    LocalizedString.statusUiSensitiveByFilter,
                    blog.filtered!!.first().title,
                )
            remember { RichText(text) }
        }
        SpoilerText(
            modifier = Modifier,
            hideContent = hideContent,
            spoilerText = humanizedSpoilerText,
            fontSize = style.contentSize,
            onShowContent = { hideContent = false },
            onHideContent = { hideContent = true },
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
            onMentionDidClick = onMentionDidClick,
            onUrlClick = onUrlClick,
        )
        if (blog.content.isNotEmpty()) {
            AnimatedVisibility(
                visible = !hideContent,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                val humanizedContent = if (blogTranslationState?.showingTranslation == true) {
                    blogTranslationState.blogTranslation!!.getHumanizedContent(blog)
                } else {
                    blog.humanizedContent
                }
                FreadRichText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .wrapContentHeight(),
                    richText = humanizedContent,
                    maxLines = contentMaxLine,
                    onMentionClick = onMentionClick,
                    onMentionDidClick = onMentionDidClick,
                    onHashtagClick = onHashtagInStatusClick,
                    onMaybeHashtagClick = onMaybeHashtagClick,
                    onUrlClick = onUrlClick,
                    fontSizeSp = style.contentSize.value,
                )
            }
        }
    } else {
        if (!blog.title.isNullOrEmpty()) {
            Text(
                modifier = Modifier,
                text = blog.title!!,
                fontWeight = FontWeight.Bold,
                fontSize = style.titleSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        if (!blog.description.isNullOrEmpty()) {
            val topPadding = if (blog.title.isNullOrEmpty()) {
                style.contentVerticalSpacing
            } else {
                style.contentVerticalSpacing / 2
            }
            FreadRichText(
                modifier = Modifier
                    .padding(top = topPadding),
                richText = blog.humanizedDescription,
                maxLines = contentMaxLine,
                onMentionClick = onMentionClick,
                onMentionDidClick = onMentionDidClick,
                onHashtagClick = onHashtagInStatusClick,
                onMaybeHashtagClick = onMaybeHashtagClick,
                onUrlClick = onUrlClick,
                fontSizeSp = style.contentSize.value,
            )
        }
        if (
            blog.title.isNullOrEmpty() &&
            blog.description.isNullOrEmpty() &&
            blog.content.isNotEmpty()
        ) {
            val humanizedContent = if (blogTranslationState?.showingTranslation == true) {
                blogTranslationState.blogTranslation!!.getHumanizedContent(blog)
            } else {
                blog.humanizedContent
            }
            FreadRichText(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                richText = humanizedContent,
                maxLines = contentMaxLine,
                onMentionClick = onMentionClick,
                onMentionDidClick = onMentionDidClick,
                onHashtagClick = onHashtagInStatusClick,
                onMaybeHashtagClick = onMaybeHashtagClick,
                fontSizeSp = style.contentSize.value,
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
    onShowContent: () -> Unit,
    onHideContent: () -> Unit,
    onUrlClick: (url: String) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onMentionClick: (Mention) -> Unit,
    onMentionDidClick: (String) -> Unit,
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
            onMentionDidClick = onMentionDidClick,
            onHashtagClick = onHashtagInStatusClick,
            onUrlClick = onUrlClick,
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
