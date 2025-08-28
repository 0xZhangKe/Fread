package com.zhangke.fread.status.ui.action

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ModalDropdownMenuItem(
    imageVector: ImageVector,
    text: String,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(text = text)
        },
        colors = colors,
        leadingIcon = {
            Icon(
                imageVector = imageVector,
                contentDescription = text,
            )
        },
        onClick = onClick,
    )
}
