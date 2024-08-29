package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zhangke.fread.framework.R

@Composable
fun AlertConfirmDialog(
    content: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    FreadDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.alert),
        contentText = content,
        positiveButtonText = stringResource(R.string.ok),
        onPositiveClick = {
            onDismissRequest()
            onConfirm()
        },
        negativeButtonText = stringResource(R.string.cancel),
        onNegativeClick = onDismissRequest,
    )
}
