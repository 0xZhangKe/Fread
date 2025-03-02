package com.zhangke.fread.status.ui.embed

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogEmbed
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
internal fun BlogEmbedsUi(
    modifier: Modifier,
    embeds: List<BlogEmbed>,
    isOwner: Boolean,
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
    if (embeds.isEmpty()) return
    embeds.forEach { embed ->
        BlogEmbedUi(
            modifier = modifier,
            embed = embed,
            style = style,
            isOwner = isOwner,
            onUrlClick = onUrlClick,
            onContentClick = onContentClick,
            onMediaClick = onMediaClick,
            onUserInfoClick = onUserInfoClick,
            onHashtagInStatusClick = onHashtagInStatusClick,
            onVoted = onVoted,
            onMentionClick = onMentionClick,
            onMentionDidClick = onMentionDidClick,
        )
    }
}

@Composable
private fun BlogEmbedUi(
    modifier: Modifier,
    embed: BlogEmbed,
    style: StatusStyle,
    isOwner: Boolean,
    onContentClick: (Blog) -> Unit,
    onMediaClick: OnBlogMediaClick,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onUrlClick: (url: String) -> Unit,
    onVoted: (List<BlogPoll.Option>) -> Unit,
    onMentionClick: (Mention) -> Unit,
    onMentionDidClick: (String) -> Unit,
) {
    when (embed) {
        is BlogEmbed.Link -> {
            StatusEmbedLinkUi(
                modifier = modifier
                    .embedBorder()
                    .padding(top = style.contentStyle.contentVerticalSpacing)
                    .fillMaxWidth(),
                linkEmbed = embed,
                style = style.cardStyle,
                onCardClick = { onUrlClick(embed.url) },
            )
        }

        is BlogEmbed.Blog -> {
            EmbedBlog(
                modifier = modifier
                    .embedBorder(),
                blog = embed.blog,
                isOwner = isOwner,
                style = style,
                onContentClick = onContentClick,
                onMediaClick = onMediaClick,
                onUserInfoClick = onUserInfoClick,
                onHashtagInStatusClick = onHashtagInStatusClick,
                onUrlClick = onUrlClick,
                onVoted = onVoted,
                onMentionClick = onMentionClick,
                onMentionDidClick = onMentionDidClick,
            )
        }
    }
}

@Composable
fun Modifier.embedBorder(): Modifier {
    return this.border(
        width = 1.dp,
        color = DividerDefaults.color,
        shape = RoundedCornerShape(8.dp),
    )
}
