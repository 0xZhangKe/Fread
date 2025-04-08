package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.richtext.FreadRichText

@Composable
fun NotificationHeadLine(
    modifier: Modifier,
    icon: ImageVector,
    avatar: String?,
    accountName: RichText?,
    interactionDesc: String,
    style: NotificationStyle,
    iconTint: Color = LocalContentColor.current,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(style.typeLogoSize),
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
        )

        if (!avatar.isNullOrEmpty()) {
            BlogAuthorAvatar(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(style.triggerAccountAvatarSize),
                imageUrl = avatar,
            )
        }

        if (accountName != null) {
            FreadRichText(
                modifier = Modifier.padding(start = 6.dp),
                richText = accountName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            modifier = Modifier.padding(start = 2.dp),
            text = interactionDesc,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
