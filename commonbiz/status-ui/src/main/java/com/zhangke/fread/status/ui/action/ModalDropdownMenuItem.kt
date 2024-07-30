package com.zhangke.fread.status.ui.action

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ModalDropdownMenuItem(
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(text = text)
        },
        leadingIcon = {
            Icon(
                imageVector = imageVector,
                contentDescription = text,
            )
        },
        onClick = onClick,
    )
}
