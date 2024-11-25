package com.zhangke.fread.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_notification_follow_request
import com.zhangke.fread.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import org.jetbrains.compose.resources.stringResource

@Composable
fun FollowRequestNotification(
    notification: NotificationUiState,
    style: NotificationStyle,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onRejectClick: (NotificationUiState) -> Unit,
    onAcceptClick: (NotificationUiState) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        NotificationHeadLine(
            modifier = Modifier.clickable {
                onUserInfoClick(notification.author)
            },
            icon = Icons.Default.PersonAddAlt1,
            avatar = null,
            accountName = null,
            interactionDesc = stringResource(Res.string.activity_pub_notification_follow_request),
            style = style,
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = style.headLineToContentPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BlogAuthorAvatar(
                modifier = Modifier
                    .size(style.statusStyle.infoLineStyle.avatarSize),
                imageUrl = notification.account.avatar,
            )

            Column(
                modifier = Modifier.weight(1F).padding(start = 6.dp, end = 6.dp),
            ) {
                Text(
                    modifier = Modifier,
                    textAlign = TextAlign.Left,
                    text = notification.account.displayName.take(style.nameMaxLength),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    textAlign = TextAlign.Left,
                    text = notification.account.acct,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }


            SimpleIconButton(
                modifier = Modifier
                    .size(32.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                onClick = {
                    onRejectClick(notification)
                },
                imageVector = Icons.Default.Clear,
                contentDescription = "Reject",
            )

            Spacer(Modifier.width(8.dp))
            SimpleIconButton(
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                onClick = {
                    onAcceptClick(notification)
                },
                imageVector = Icons.Default.Check,
                contentDescription = "Accept",
            )
        }
    }
}
