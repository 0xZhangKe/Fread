package com.zhangke.fread.status.ui.embed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.common.ext.formatDefault
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.common.utils.DateTimeFormatter
import com.zhangke.fread.common.utils.defaultFormatConfig
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.ui.BlogContent
import com.zhangke.fread.status.ui.StatusInfoLine
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun EmbedBlog(
    modifier: Modifier,
    blog: Blog,
    style: StatusStyle,
    onContentClick: (Blog) -> Unit,
    onMediaClick: OnBlogMediaClick,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onUrlClick: (url: String) -> Unit,
    onVoted: (List<BlogPoll.Option>) -> Unit,
    onMentionClick: (Mention) -> Unit,
    onMentionDidClick: (String) -> Unit,
) {
    Column(
        modifier = modifier.noRippleClick { onContentClick(blog) },
    ) {
        val datetime = blog.date.instant.toEpochMilliseconds()
        StatusInfoLine(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            blogTranslationState = remember { BlogTranslationUiState(support = false) },
            blogAuthor = blog.author,
            displayTime = produceState("") {
                DateTimeFormatter.format(datetime, defaultFormatConfig())
            }.value,
            visibility = blog.visibility,
            blogUrl = blog.url,
            showFollowButton = false,
            moreInteractions = emptyList(),
            showMoreOperationIcon = false,
            onInteractive = {},
            onUserInfoClick = onUserInfoClick,
            onUrlClick = onUrlClick,
            onFollowClick = {},
            style = style,
            reblogAuthor = null,
            editedAt = blog.editedAt?.instant,
            onTranslateClick = {},
        )
        BlogContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = style.containerStartPadding + style.contentStyle.startPadding,
                    top = style.contentStyle.contentVerticalSpacing,
                    end = style.containerEndPadding,
                ),
            blog = blog,
            blogTranslationState = remember { BlogTranslationUiState(support = false) },
            specificTime = remember {
                blog.date.instant.formatDefault()
            },
            detailModel = false,
            indexOfFeeds = 0,
            style = style,
            onMediaClick = onMediaClick,
            onVoted = onVoted,
            onUrlClick = onUrlClick,
            onBoostedClick = {},
            onFavouritedClick = {},
            editedTime = remember { blog.editedAt?.instant?.formatDefault() },
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
            onMentionDidClick = onMentionDidClick,
            onShowOriginalClick = {},
            onBlogClick = onContentClick,
            onUserInfoClick = onUserInfoClick,
        )
    }
}
