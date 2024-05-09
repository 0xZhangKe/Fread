package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.status.ui.BlogAuthorAvatar

@Composable
fun NotificationHeadLine(
    modifier: Modifier,
    icon: ImageVector,
    avatar: String?,
    accountName: String?,
    interactionDesc: String,
    style: NotificationStyle,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(style.typeLogoSize),
            imageVector = icon,
            contentDescription = null,
        )

        if (!avatar.isNullOrEmpty()) {
            BlogAuthorAvatar(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(style.triggerAccountAvatarSize),
                imageUrl = avatar,
            )
        }

        if (!accountName.isNullOrEmpty()) {
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = accountName.take(style.nameMaxLength),
            )
        }

        Text(
            modifier = Modifier.padding(start = 2.dp),
            text = interactionDesc,
        )
    }
}
