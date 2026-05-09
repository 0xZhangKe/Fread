package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.framework.composable.TwoTextsInRow
import com.zhangke.fread.status.model.FormattingTime
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.richtext.FreadRichText

private const val MAX_STACKED_AVATARS = 4

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
    additionalAvatars: List<String?> = emptyList(),
    othersCount: Int = 0,
    avatarSize: Dp = style.triggerAccountAvatarSize,
    expandable: Boolean = false,
    expanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
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

        val stack = (listOf(avatar) + additionalAvatars)
            .filter { !it.isNullOrEmpty() }
            .take(MAX_STACKED_AVATARS)
        if (stack.isNotEmpty()) {
            AvatarStack(
                modifier = Modifier.padding(start = 6.dp),
                avatars = stack,
                avatarSize = avatarSize,
            )
        }

        val displayDesc = if (othersCount > 0) {
            val plural = if (othersCount == 1) "other" else "others"
            "and $othersCount $plural $interactionDesc"
        } else {
            interactionDesc
        }
        TwoTextsInRow(
            modifier = Modifier.weight(1F),
            firstText = {
                FreadRichText(
                    modifier = Modifier.padding(start = 6.dp),
                    richText = accountName ?: RichText.empty,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                )
            },
            secondText = {
                Text(
                    modifier = Modifier,
                    text = displayDesc,
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

        if (expandable) {
            Icon(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(18.dp)
                    .clickable { onToggleExpand() },
                imageVector = if (expanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AvatarStack(
    modifier: Modifier,
    avatars: List<String?>,
    avatarSize: Dp,
) {
    val overlap = avatarSize * 0.35F
    Box(modifier = modifier) {
        avatars.forEachIndexed { index, url ->
            BlogAuthorAvatar(
                modifier = Modifier
                    .padding(start = (avatarSize - overlap) * index)
                    .size(avatarSize),
                imageUrl = url,
            )
        }
    }
}
