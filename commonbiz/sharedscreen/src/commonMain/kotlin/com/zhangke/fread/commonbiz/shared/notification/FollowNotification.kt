package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.fread.commonbiz.shared.composable.UserInfoCard
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_follow_desc
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.richtext.FreadRichText
import org.jetbrains.compose.resources.stringResource

@Composable
fun FollowNotification(
    notification: StatusNotification.Follow,
    style: NotificationStyle,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onFollowAccountClick: (BlogAuthor) -> Unit,
    onUnfollowAccountClick: (BlogAuthor) -> Unit,
    onUnblockClick: (BlogAuthor) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(style.containerPaddings),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUserInfoClick(notification.author) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(style.typeLogoSize),
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
            )
            BlogAuthorAvatar(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(style.triggerAccountAvatarSize),
                imageUrl = notification.author.avatar,
            )
            FreadRichText(
                modifier = Modifier.padding(start = 6.dp),
                richText = notification.author.humanizedName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = stringResource(Res.string.shared_notification_follow_desc),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        UserInfoCard(
            modifier = Modifier.padding(top = style.headLineToContentPadding)
                .fillMaxWidth(),
            user = notification.author,
            onUserClick = onUserInfoClick,
            onFollowAccountClick = onFollowAccountClick,
            onUnfollowAccountClick = onUnfollowAccountClick,
            onUnblockClick = onUnblockClick,
        )
    }
}
