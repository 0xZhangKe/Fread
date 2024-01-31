package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.voyager.pushDestination
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.activitypub.app.internal.screen.user.UserDetailRoute
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.ui.BlogAuthorAvatar

@Composable
fun FollowNotification(
    notification: NotificationUiState,
    style: NotificationStyle,
) {
    val navigator = LocalNavigator.currentOrThrow
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigator.pushDestination(
                    UserDetailRoute.buildRoute(notification.accountUri)
                )
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Icon(
            modifier = Modifier.size(style.typeLogoSize),
            imageVector = Icons.Default.Headphones,
            contentDescription = null,
        )

        BlogAuthorAvatar(
            modifier = Modifier
                .padding(start = 6.dp)
                .size(style.triggerAccountAvatarSize),
            imageUrl = notification.account.avatar,
        )

        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = notification.account.displayName.take(style.nameMaxLength),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = stringResource(R.string.activity_pub_notification_follow_desc),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
