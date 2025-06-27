package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.commonbiz.shared.composable.OnlyBlogContentUi
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.ComposedStatusInteraction

/**
 * 关于你自己发布的博客的通知UI。
 * 例如你发布的帖子被别人点赞、转发、收藏等。
 */
@Composable
fun BlogInteractionNotification(
    blog: Blog,
    locator: PlatformLocator,
    author: BlogAuthor,
    icon: ImageVector,
    interactionDesc: String,
    indexInList: Int,
    style: NotificationStyle,
    composedStatusInteraction: ComposedStatusInteraction,
    iconTint: Color = LocalContentColor.current,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    Column(
        modifier = Modifier
            .clickable { composedStatusInteraction.onBlogClick(locator, blog) }
            .fillMaxWidth()
            .padding(style.containerPaddings),
    ) {
        NotificationHeadLine(
            modifier = Modifier.clickable {
                composedStatusInteraction.onUserInfoClick(locator, author)
            },
            icon = icon,
            avatar = author.avatar,
            accountName = author.humanizedName,
            interactionDesc = interactionDesc,
            style = style,
            iconTint = iconTint,
        )
        OnlyBlogContentUi(
            modifier = Modifier
                .padding(top = style.headLineToContentPadding)
                .statusBorder()
                .padding(style.internalBlogPadding),
            blog = blog,
            isOwner = true,
            indexInList = indexInList,
            style = style.statusStyle,
            onVoted = {},
            onHashtagInStatusClick = {
                composedStatusInteraction.onHashtagInStatusClick(locator, it)
            },
            onMentionClick = {
                composedStatusInteraction.onMentionClick(locator, it)
            },
            onUrlClick = {
                browserLauncher.launchWebTabInApp(it, locator)
            },
            onMentionDidClick = {
                composedStatusInteraction.onMentionClick(
                    locator = locator,
                    did = it,
                    protocol = blog.platform.protocol,
                )
            },
            onMaybeHashtagClick = {
                composedStatusInteraction.onMaybeHashtagClick(
                    locator = locator,
                    protocol = blog.platform.protocol,
                    hashtag = it,
                )
            },
        )
    }
}

@Composable
fun Modifier.statusBorder(show: Boolean = true): Modifier {
    return if (show) {
        this.border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
    } else {
        this
    }
}
