package com.zhangke.utopia.status.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
        var hideContent by remember(spoilerText) {
            mutableStateOf(true)
        }
        SpoilerText(
            hideContent = hideContent,
            spoilerText = blog.humanizedSpoilerText,
            onShowContent = { hideContent = false },
            onHideContent = { hideContent = true },
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
        )
        if (blog.content.isNotEmpty() && !hideContent) {
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
    Card(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
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
                .wrapContentHeight(),
            richText = spoilerText,
            onMentionClick = onMentionClick,
            onHashtagClick = onHashtagInStatusClick,
            textSelectable = textSelectable,
        )
    }
}
