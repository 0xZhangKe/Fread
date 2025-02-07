package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zhangke.fread.commonbiz.shared.composable.WholeBlogUi
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.ui.ComposedStatusInteraction

@Composable
fun NotificationWithWholeStatus(
    status: StatusUiState,
    author: BlogAuthor,
    indexInList: Int,
    icon: ImageVector,
    interactionDesc: String,
    style: NotificationStyle,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { composedStatusInteraction.onStatusClick(status) }
            .padding(vertical = 8.dp)
    ) {
        NotificationHeadLine(
            modifier = Modifier.clickable {
                composedStatusInteraction.onUserInfoClick(status.role, author)
            },
            icon = icon,
            avatar = author.avatar,
            accountName = author.humanizedName,
            interactionDesc = interactionDesc,
            style = style,
        )

        WholeBlogUi(
            modifier = Modifier
                .padding(top = style.headLineToContentPadding)
                .statusBorder()
                .padding(style.internalBlogPadding),
            statusUiState = status,
            indexInList = indexInList,
            style = style.statusStyle,
            showDivider = false,
            composedStatusInteraction = composedStatusInteraction,
        )
    }
}
