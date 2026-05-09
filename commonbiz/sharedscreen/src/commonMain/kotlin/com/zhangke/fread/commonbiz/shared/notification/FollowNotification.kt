package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    additionalActors: List<BlogAuthor> = emptyList(),
) {
    var expanded by rememberSaveable(notification.id) { mutableStateOf(false) }
    val expandable = additionalActors.isNotEmpty()
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(style.containerPaddings),
    ) {
        NotificationHeadLine(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUserInfoClick(notification.author) },
            icon = Icons.Default.PersonAdd,
            avatar = notification.author.avatar,
            createAt = notification.formattingDisplayTime,
            accountName = notification.author.humanizedName,
            interactionDesc = stringResource(LocalizedString.sharedNotificationFollowDesc),
            style = style,
            additionalAvatars = additionalActors.map { it.avatar },
            othersCount = additionalActors.size,
            avatarSize = style.triggerAccountAvatarSize * 1.5F,
            expandable = expandable,
            expanded = expanded,
            onToggleExpand = { expanded = !expanded },
        )

        if (expandable && expanded) {
            val allActors = listOf(notification.author) + additionalActors
            NotificationActorsList(
                modifier = Modifier.padding(top = style.headLineToContentPadding),
                actors = allActors,
                onActorClick = onUserInfoClick,
            )
        }
    }
}
