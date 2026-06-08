package com.zhangke.fread.status.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.ui.common.BlogTextContentSection
import com.zhangke.fread.status.ui.common.BlogTranslateLabel
import com.zhangke.fread.status.ui.embed.BlogEmbedsUi
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.label.StatusBottomEditedLabel
import com.zhangke.fread.status.ui.label.StatusBottomInteractionLabel
import com.zhangke.fread.status.ui.label.StatusBottomTimeLabel
import com.zhangke.fread.status.ui.media.BlogMedias
import com.zhangke.fread.status.ui.model.BlogUIType
import com.zhangke.fread.status.ui.poll.BlogPoll
import com.zhangke.fread.status.ui.style.StatusStyle

/**
 * 博客正文部分，仅包含内容，投票，媒体，链接预览卡片。
 */
@Composable
fun BlogContent(
    modifier: Modifier,
    blog: Blog,
    type: BlogUIType,
    isOwner: Boolean?,
    style: StatusStyle,
    indexOfFeeds: Int,
    sharedElementId: String? = null,
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
    onUnavailableQuoteClick: (String) -> Unit,
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
        if (type == BlogUIType.DETAIL) {
            SelectionContainer {
                Column {
                    BlogTextContentSection(
                        blog = blog,
                        type = BlogUIType.DETAIL,
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
                type = type,
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
        }
        if (blog.poll == null && blog.mediaList.isNotEmpty()) {
            BlogMedias(
                modifier = Modifier
                    .padding(top = style.contentStyle.contentVerticalSpacing)
                    .fillMaxWidth(),
                mediaList = blog.mediaList,
                sharedElementId = sharedElementId ?: blog.id,
                blogTranslationState = blogTranslationState,
                indexInList = indexOfFeeds,
                sensitive = sensitive,
                onMediaClick = onMediaClick,
            )
        }
        if (blog.poll == null && blog.embeds.isNotEmpty()) {
            BlogEmbedsUi(
                modifier = Modifier
                    .padding(top = style.contentStyle.contentVerticalSpacing)
                    .fillMaxWidth(),
                embeds = blog.embeds,
                style = style,
                onContentClick = onBlogClick,
                onUrlClick = onUrlClick,
                onUnavailableQuoteClick = onUnavailableQuoteClick,
            )
        }

        if (type == BlogUIType.DETAIL) {
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
