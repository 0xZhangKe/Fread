package com.zhangke.fread.status.ui.reply

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.BlogTextContentSection
import com.zhangke.fread.status.ui.media.BlogMedias
import com.zhangke.fread.status.ui.publish.NameAndAccountInfo
import com.zhangke.fread.status.ui.publish.PublishBlogStyle
import com.zhangke.fread.status.ui.publish.PublishBlogStyleDefault

@Composable
fun BlogInReply(
    modifier: Modifier,
    blog: Blog,
    style: PublishBlogStyle = PublishBlogStyleDefault.defaultStyle(),
) {
    Row(
        modifier = modifier,
    ) {
        BlogAuthorAvatar(
            modifier = Modifier.size(style.avatarSize),
            imageUrl = blog.author.avatar,
        )
        Column(
            modifier = Modifier.weight(1F).padding(start = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NameAndAccountInfo(
                    modifier = Modifier.weight(1F),
                    name = blog.author.name,
                    handle = blog.author.prettyHandle,
                    style = style,
                )
                Text(
                    text = blog.formattingDisplayTime.formattedTime(),
                    style = style.handleStyle,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Column(modifier = Modifier.weight(3F)) {
                    BlogTextContentSection(
                        blog = blog,
                        style = style.contentStyle,
                        onHashtagInStatusClick = {},
                        onMentionDidClick = {},
                        onMentionClick = {},
                        onUrlClick = {},
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                BlogMedias(
                    modifier = Modifier.weight(1F),
                    mediaList = blog.mediaList,
                    indexInList = 0,
                    sensitive = blog.sensitive,
                    onMediaClick = {},
                )
            }
        }
    }
}
