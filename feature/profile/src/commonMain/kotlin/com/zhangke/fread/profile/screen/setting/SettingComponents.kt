package com.zhangke.fread.profile.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.PopupMenu
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val itemHeight = 82.dp

@Composable
internal fun SettingItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChangeRequest: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1F)) {
            SettingItem(
                icon = icon,
                title = title,
                subtitle = subtitle,
                onClick = {},
            )
        }
        Switch(
            modifier = Modifier,
            checked = checked,
            onCheckedChange = onCheckedChangeRequest,
        )
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
internal fun SettingItemWithPopup(
    icon: ImageVector,
    title: String,
    subtitle: String,
    dropDownItemCount: Int,
    dropDownItemText: @Composable (Int) -> String,
    onItemClick: (Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxWidth()) {
        var showPopup by remember {
            mutableStateOf(false)
        }
        SettingItem(
            icon = icon,
            title = title,
            subtitle = subtitle,
            onClick = {
                showPopup = true
            },
        )
        PopupMenu(
            expanded = showPopup,
            offset = DpOffset(x = 36.dp, y = 0.dp),
            onDismissRequest = { showPopup = false },
        ) {
            repeat(dropDownItemCount) { index ->
                DropdownMenuItem(
                    text = { Text(dropDownItemText(index)) },
                    onClick = {
                        showPopup = false
                        coroutineScope.launch {
                            delay(100)
                            onItemClick(index)
                        }
                    },
                )
            }
        }
    }
}

@Composable
internal fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    redDot: Boolean = false,
    onClick: () -> Unit,
) {
    SettingItem(
        icon = {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon,
                contentDescription = title,
            )
        },
        title = title,
        redDot = redDot,
        subtitle = subtitle,
        onClick = onClick,
    )
}

@Composable
internal fun SettingItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String?,
    redDot: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = itemHeight)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                    )
                    if (redDot) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier.size(4.dp)
                                .clip(CircleShape)
                                .background(Color.Red.copy(alpha = 0.8F)),
                        )
                    }
                }
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
