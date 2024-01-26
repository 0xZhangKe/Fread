package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.status.ui.BlogAuthorAvatar

@Composable
fun FollowRequestNotification(
    notification: NotificationUiState,
    style: NotificationStyle,
    onRejectClick: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        NotificationHeadLine(
            icon = Icons.Default.PersonAddAlt1,
            avatar = null,
            accountName = null,
            interactionDesc = stringResource(R.string.activity_pub_notification_follow_request),
            style = style,
        )

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (avatarRef, nameRef, webFingerRef, rejectRef, acceptRef) = createRefs()
            BlogAuthorAvatar(
                modifier = Modifier
                    .size(style.statusStyle.statusInfoStyle.avatarSize)
                    .constrainAs(avatarRef) {
                        top.linkTo(nameRef.top)
                        start.linkTo(parent.start)
                    },
                imageUrl = notification.account.avatar,
            )

            Text(
                modifier = Modifier
                    .constrainAs(nameRef) {
                        top.linkTo(parent.top, style.headLineToContentPadding)
                        start.linkTo(avatarRef.end, margin = 6.dp)
                        end.linkTo(rejectRef.start, margin = 6.dp)
                        width = Dimension.fillToConstraints
                    },
                textAlign = TextAlign.Left,
                text = notification.account.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                modifier = Modifier
                    .constrainAs(webFingerRef) {
                        top.linkTo(nameRef.bottom, 2.dp)
                        start.linkTo(nameRef.start)
                        end.linkTo(nameRef.end)
                        width = Dimension.fillToConstraints
                    },
                textAlign = TextAlign.Left,
                text = notification.account.webFinger.toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            SimpleIconButton(
                modifier = Modifier
                    .size(32.dp)
                    .constrainAs(rejectRef) {
                        top.linkTo(avatarRef.top)
                        bottom.linkTo(avatarRef.bottom)
                        end.linkTo(acceptRef.start, margin = 16.dp)
                    },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                onClick = onRejectClick,
                imageVector = Icons.Default.Clear,
                contentDescription = "Reject",
            )

            SimpleIconButton(
                modifier = Modifier
                    .size(32.dp)
                    .constrainAs(acceptRef) {
                        top.linkTo(avatarRef.top)
                        bottom.linkTo(avatarRef.bottom)
                        end.linkTo(parent.end)
                    },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                onClick = onAcceptClick,
                imageVector = Icons.Default.Check,
                contentDescription = "Accept",
            )
        }
    }
}
