package com.zhangke.fread.status.ui.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.Composable
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_interaction_copy_url
import com.zhangke.fread.statusui.status_ui_interaction_open_in_browser
import com.zhangke.fread.statusui.status_ui_interaction_open_original_instance
import org.jetbrains.compose.resources.stringResource

@Composable
fun DropDownCopyLinkItem(
    onClick: () -> Unit,
) {
    ModalDropdownMenuItem(
        text = stringResource(Res.string.status_ui_interaction_copy_url),
        imageVector = Icons.Default.ContentCopy,
        onClick = onClick,
    )
}

@Composable
fun DropDownOpenInBrowserItem(onClick: () -> Unit) {
    ModalDropdownMenuItem(
        text = stringResource(Res.string.status_ui_interaction_open_in_browser),
        imageVector = Icons.Default.Language,
        onClick = onClick,
    )
}

@Composable
fun DropDownOpenOriginalInstanceItem(onClick: () -> Unit) {
    ModalDropdownMenuItem(
        text = stringResource(Res.string.status_ui_interaction_open_original_instance),
        imageVector = Icons.Default.CloudQueue,
        onClick = onClick,
    )
}
