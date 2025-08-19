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
import com.zhangke.fread.commonbiz.shared.composable.BlogUi
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FormattingTime
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.ComposedStatusInteraction

@Composable
fun NotificationWithWholeStatus(
    blog: Blog,
    locator: PlatformLocator,
    author: BlogAuthor?,
    createAt: FormattingTime,
    indexInList: Int,
    icon: ImageVector,
    interactionDesc: String,
    style: NotificationStyle,
    composedStatusInteraction: ComposedStatusInteraction,
    iconTint: Color = LocalContentColor.current,
) {
    Column(
        modifier = Modifier
            .clickable { composedStatusInteraction.onBlogClick(locator, blog) }
            .fillMaxWidth()
            .padding(style.containerPaddings)
    ) {
        NotificationHeadLine(
            modifier = Modifier.clickable(enabled = author != null) {
                composedStatusInteraction.onUserInfoClick(locator, author!!)
            },
            icon = icon,
            avatar = author?.avatar,
            iconTint = iconTint,
            createAt = createAt,
            accountName = author?.humanizedName,
            interactionDesc = interactionDesc,
            style = style,
        )

        BlogUi(
            modifier = Modifier.padding(top = style.headLineToContentPadding)
                .statusBorder()
                .padding(style.internalBlogPadding),
            blog = blog,
            locator = locator,
            indexInList = indexInList,
            style = style.statusStyle.copy(
                containerStartPadding = 0.dp,
                containerEndPadding = 0.dp,
                containerTopPadding = 0.dp,
                containerBottomPadding = 0.dp,
            ),
            showBottomPanel = false,
            showMoreOperationIcon = false,
            composedStatusInteraction = composedStatusInteraction,
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
