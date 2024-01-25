package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.ui.BlogAuthorAvatar
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

@Composable
fun StatusNotificationUi(
    modifier: Modifier,
    notification: NotificationUiState,
    indexInList: Int,
    style: StatusStyle = defaultStatusStyle(),
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    when (notification.type) {
        StatusNotificationType.FAVOURITE -> {
            FavouriteNotification(
                modifier = modifier,
                notification = notification,
                indexInList = indexInList,
                onInteractive = onInteractive,
            )
        }

        StatusNotificationType.MENTION -> {
            MentionNotification(
                modifier = modifier,
                notification = notification,
                indexInList = indexInList,
                onInteractive = onInteractive,
            )
        }

        else -> {
            UnknownNotification(
                modifier = modifier,
                notification = notification,
            )
        }
    }
}

@Composable
private fun FavouriteNotification(
    modifier: Modifier,
    notification: NotificationUiState,
    indexInList: Int,
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    val status = notification.status
    if (status == null) {
        UnknownNotification(
            modifier = modifier,
            notification = notification,
        )
        return
    }
    ConstraintLayout(modifier = modifier) {
        val (avatarRef, nameRef, actionDescRef, statusRef) = createRefs()
        BlogAuthorAvatar(
            modifier = Modifier
                .size(22.dp)
                .constrainAs(avatarRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            imageUrl = notification.account.avatar,
        )
        Text(
            modifier = Modifier
                .constrainAs(nameRef) {
                    start.linkTo(avatarRef.end, 4.dp)
                    top.linkTo(avatarRef.top)
                },
            text = notification.account.name.take(10),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            modifier = Modifier
                .constrainAs(actionDescRef) {
                    start.linkTo(nameRef.end, 2.dp)
                    baseline.linkTo(nameRef.baseline)
                },
            text = stringResource(R.string.activity_pub_notification_favourited_desc),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        BlogUiInNotification(
            modifier = Modifier
                .constrainAs(statusRef) {
                    top.linkTo(nameRef.bottom, 4.dp)
                    start.linkTo(nameRef.start)
                    end.linkTo(parent.end)
                }
                .border(1.dp, Color.Gray, MaterialTheme.shapes.medium),
            statusUiState = status,
            indexInList = indexInList,
            onInteractive = onInteractive,
        )
    }
}

@Composable
private fun MentionNotification(
    modifier: Modifier,
    notification: NotificationUiState,
    indexInList: Int,
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    val status = notification.status
    if (status == null) {
        UnknownNotification(
            modifier = modifier,
            notification = notification,
        )
        return
    }
    BlogUiInNotification(
        modifier = modifier,
        statusUiState = status,
        indexInList = indexInList,
        onInteractive = onInteractive,
    )
}

@Composable
private fun UnknownNotification(
    modifier: Modifier,
    notification: NotificationUiState,
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.activity_pub_notification_unknown_desc),
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "${notification.type.name} from ${notification.account.name}",
        )
    }
}
