package com.zhangke.fread.status.ui.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zhangke.fread.statusui.R

@Composable
fun DropDownCopyLinkItem(
    onClick: () -> Unit,
) {
    ModalDropdownMenuItem(
        text = stringResource(R.string.status_ui_interaction_copy_url),
        imageVector = Icons.Default.ContentCopy,
        onClick = onClick,
    )
}

@Composable
fun DropDownOpenInBrowserItem(onClick: () -> Unit) {
    ModalDropdownMenuItem(
        text = stringResource(R.string.status_ui_interaction_open_in_browser),
        imageVector = Icons.Default.Language,
        onClick = onClick,
    )
}
