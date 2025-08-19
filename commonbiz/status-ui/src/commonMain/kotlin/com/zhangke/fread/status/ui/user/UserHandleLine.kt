package com.zhangke.fread.status.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.fread.statusui.status_ui_user_detail_follows_you
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserHandleLine(
    modifier: Modifier,
    handle: String,
    bot: Boolean,
    followedBy: Boolean,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (bot) {
            Icon(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(16.dp),
                imageVector = Icons.Outlined.SmartToy,
                contentDescription = "Bot",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        SelectionContainer {
            Text(
                modifier = Modifier,
                text = handle,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium
                    .copy(fontWeight = FontWeight.Normal),
            )
        }
        if (followedBy) {
            Text(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(2.dp),
                    )
                    .padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = stringResource(com.zhangke.fread.statusui.Res.string.status_ui_user_detail_follows_you),
                style = MaterialTheme.typography.bodySmall
                    .copy(fontWeight = FontWeight.Normal),
            )
        }
    }
}
