package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_severed_desc
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.notification.StatusNotification
import org.jetbrains.compose.resources.stringResource

@Composable
fun SeveredRelationshipsNotification(
    notification: StatusNotification.SeveredRelationships,
    style: NotificationStyle,
    onUserInfoClick: (BlogAuthor) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserInfoClick(notification.author) }
            .padding(style.containerPaddings),
    ) {
        NotificationHeadLine(
            modifier = Modifier,
            icon = Icons.Default.WarningAmber,
            avatar = notification.author.avatar,
            createAt = notification.formattingDisplayTime,
            accountName = notification.author.humanizedName,
            interactionDesc = stringResource(Res.string.shared_notification_severed_desc),
            style = style,
        )

        Text(
            modifier = Modifier
                .padding(top = style.headLineToContentPadding)
                .align(Alignment.CenterHorizontally),
            text = notification.reason,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
