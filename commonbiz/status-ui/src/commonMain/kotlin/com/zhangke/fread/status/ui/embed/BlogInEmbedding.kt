package com.zhangke.fread.status.ui.embed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogEmbed
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.BlogTextContentSection
import com.zhangke.fread.status.ui.media.BlogMedias
import com.zhangke.fread.status.ui.publish.NameAndAccountInfo
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun BlogInEmbedding(
    modifier: Modifier,
    blog: Blog,
    style: StatusStyle,
    onContentClick: (Blog) -> Unit = {},
) {
    Row(
        modifier = modifier.noRippleClick { onContentClick(blog) },
    ) {
        BlogAuthorAvatar(
            modifier = Modifier.size(style.infoLineStyle.avatarSize),
            imageUrl = blog.author.avatar,
        )
        Column(
            modifier = Modifier.weight(1F).padding(start = 8.dp),
        ) {
            // Name\handle\time row
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
            ) {
                NameAndAccountInfo(
                    modifier = Modifier.weight(1F)
                        .alignByBaseline(),
                    humanizedName = blog.author.humanizedName,
                    handle = blog.author.prettyHandle,
                    style = style,
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp)
                        .alignByBaseline(),
                    text = blog.formattingDisplayTime.formattedTime(),
                    style = style.infoLineStyle.descStyle,
                    color = style.secondaryFontColor,
                )
            }

            // text content and images
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
            ) {
                Column(modifier = Modifier.weight(3F)) {
                    BlogTextContentSection(
                        blog = blog,
                        style = style.contentStyle.copy(
                            maxLine = 10
                        ),
                    )
                }
                if (blog.mediaList.isNotEmpty()) {
                    Spacer(modifier = Modifier.size(8.dp))
                    BlogMedias(
                        modifier = Modifier.weight(1F),
                        mediaList = blog.mediaList,
                        indexInList = 0,
                        sensitive = blog.sensitive,
                        onMediaClick = {},
                        showAlt = false,
                    )
                }
            }

            // link card
            val linkEmbed = blog.embeds.firstNotNullOfOrNull { it as? BlogEmbed.Link }
            if (linkEmbed != null) {
                Spacer(modifier = Modifier.height(style.contentStyle.contentVerticalSpacing))
                StatusEmbedLinkUi(
                    modifier = Modifier.fillMaxWidth()
                        .embedBorder(),
                    style = style.cardStyle,
                    linkEmbed = linkEmbed,
                    onCardClick = {},
                )
            }
        }
    }
}
