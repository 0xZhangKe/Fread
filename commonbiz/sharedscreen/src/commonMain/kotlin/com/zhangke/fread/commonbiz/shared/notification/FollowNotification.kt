package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.commonbiz.shared.composable.UserInfoCard
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.notification.StatusNotification
import org.jetbrains.compose.resources.stringResource

@Composable
fun FollowNotification(
    notification: StatusNotification.Follow,
    style: NotificationStyle,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onFollowAccountClick: (BlogAuthor) -> Unit,
    onUnfollowAccountClick: (BlogAuthor) -> Unit,
    onUnblockClick: (BlogAuthor) -> Unit,
    onCancelFollowRequestClick: (BlogAuthor) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(style.containerPaddings),
    ) {
        NotificationHeadLine(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.PersonAdd,
            avatar = notification.author.avatar,
            createAt = notification.formattingDisplayTime,
            accountName = notification.author.humanizedName,
            interactionDesc = stringResource(LocalizedString.sharedNotificationFollowDesc),
            style = style,
        )
        UserInfoCard(
            modifier = Modifier.padding(top = style.headLineToContentPadding)
                .fillMaxWidth(),
            user = notification.author,
            onUserClick = onUserInfoClick,
            onFollowAccountClick = onFollowAccountClick,
            onUnfollowAccountClick = onUnfollowAccountClick,
            onUnblockClick = onUnblockClick,
            onCancelFollowRequestClick = onCancelFollowRequestClick,
        )
    }
}
