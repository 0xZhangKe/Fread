package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState

@Composable
fun SeveredRelationshipsNotification(
    notification: NotificationUiState,
    style: NotificationStyle,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        NotificationHeadLine(
            icon = Icons.Default.WarningAmber,
            avatar = notification.account.avatar,
            accountName = notification.account.displayName,
            interactionDesc = stringResource(R.string.activity_pub_notification_severed_desc),
            style = style,
        )

        Text(
            modifier = Modifier
                .padding(top = style.headLineToContentPadding)
                .align(Alignment.CenterHorizontally),
            text = notification.relationshipSeveranceEvent?.targetName.ifNullOrEmpty { "Unknown!" },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
