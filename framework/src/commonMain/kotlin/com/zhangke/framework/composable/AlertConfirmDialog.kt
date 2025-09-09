package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlertConfirmDialog(
    content: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    FreadDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(LocalizedString.alert),
        contentText = content,
        positiveButtonText = stringResource(LocalizedString.ok),
        onPositiveClick = {
            onDismissRequest()
            onConfirm()
        },
        negativeButtonText = stringResource(LocalizedString.cancel),
        onNegativeClick = onDismissRequest,
    )
}
