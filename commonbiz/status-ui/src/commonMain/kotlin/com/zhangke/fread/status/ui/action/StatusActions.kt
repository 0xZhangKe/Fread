package com.zhangke.fread.status.ui.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.runtime.Composable
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource

@Composable
fun DropDownCopyLinkItem(
    onClick: () -> Unit,
) {
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.statusUiInteractionCopyUrl),
        imageVector = Icons.Default.ContentCopy,
        onClick = onClick,
    )
}

@Composable
fun DropDownOpenInBrowserItem(onClick: () -> Unit) {
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.statusUiInteractionOpenInBrowser),
        imageVector = Icons.Default.Language,
        onClick = onClick,
    )
}

@Composable
fun DropDownOpenOriginalInstanceItem(onClick: () -> Unit) {
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.statusUiInteractionOpenOriginalInstance),
        imageVector = Icons.Default.CloudQueue,
        onClick = onClick,
    )
}

@Composable
fun DropDownOpenStatusByOtherAccountItem(onClick: () -> Unit) {
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.statusUiInteractionOpenBlogByOtherAccount),
        imageVector = Icons.Default.PersonSearch,
        onClick = onClick,
    )
}
