package com.zhangke.utopia.status.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.media.BlogMedias
import com.zhangke.utopia.status.ui.poll.BlogPoll
import com.zhangke.utopia.status.ui.richtext.UtopiaRichText
import com.zhangke.utopia.status.ui.style.BlogStyle
import com.zhangke.utopia.statusui.R

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
    onHashtagInStatusClick: (BlogAuthor, HashtagInStatus) -> Unit,
    onMentionClick: (BlogAuthor, Mention) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        val sensitive = blog.sensitive
        val spoilerText = blog.spoilerText
        val canHidden = blog.sensitive || spoilerText.isNotEmpty()
        var hideContent by rememberSaveable {
            mutableStateOf(canHidden)
        }
        if (spoilerText.isNotEmpty()) {
            // todo font size
            UtopiaRichText(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 15.dp, end = 15.dp, top = 8.dp),
                richText = blog.humanizedSpoilerText,
                onMentionClick = {
                    onMentionClick(blog.author, it)
                },
                onHashtagClick = { hashtag ->
                    onHashtagInStatusClick(blog.author, hashtag)
                },
            )
        }
        val hasContent = blog.content.isNotEmpty()
        if (hasContent) {
            if (hideContent) {
                TextButton(
                    onClick = {
                        hideContent = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Transparent,
                    )
                ) {
                    Text(text = stringResource(R.string.status_ui_image_content_show_hidden_label))
                }
            }
            if (!hideContent) {
                // todo font size
                UtopiaRichText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 15.dp, end = 15.dp, top = 4.dp),
                    richText = blog.humanizedContent,
                    maxLines = style.contentMaxLine,
                    onMentionClick = {
                        onMentionClick(blog.author, it)
                    },
                    onHashtagClick = { hashtag ->
                        onHashtagInStatusClick(blog.author, hashtag)
                    },
                )
                if (canHidden) {
                    TextButton(
                        onClick = {
                            hideContent = true
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Transparent,
                        )
                    ) {
                        Text(text = stringResource(R.string.status_ui_image_content_hide_hidden_label))
                    }
                }
            }
        }

        if (blog.mediaList.isNotEmpty()) {
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
            BlogPoll(
                modifier = Modifier.fillMaxWidth(),
                poll = blog.poll!!,
                onVoted = onVoted,
            )
        }
    }
}
