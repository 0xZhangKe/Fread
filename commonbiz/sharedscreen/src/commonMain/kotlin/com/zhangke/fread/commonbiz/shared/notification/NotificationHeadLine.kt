package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.TwoTextsInRow
import com.zhangke.fread.status.model.FormattingTime
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
    createAt: FormattingTime,
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

        TwoTextsInRow(
            modifier = Modifier.weight(1F),
            firstText = {
                if (accountName != null) {
                    FreadRichText(
                        modifier = Modifier.padding(start = 6.dp),
                        richText = accountName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSizeSp = 12F,
                    )
                }
            },
            secondText = {
                Text(
                    modifier = Modifier,
                    text = interactionDesc,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                )
            },
            spacing = 2.dp,
        )

        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = createAt.formattedTime(),
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
