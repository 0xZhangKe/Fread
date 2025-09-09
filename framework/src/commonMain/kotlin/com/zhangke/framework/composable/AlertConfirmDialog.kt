package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import com.zhangke.fread.localization.Res
import com.zhangke.fread.localization.alert
import com.zhangke.fread.localization.cancel
import com.zhangke.fread.localization.ok
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlertConfirmDialog(
    content: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    FreadDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(Res.string.alert),
        contentText = content,
        positiveButtonText = stringResource(Res.string.ok),
        onPositiveClick = {
            onDismissRequest()
            onConfirm()
        },
        negativeButtonText = stringResource(Res.string.cancel),
        onNegativeClick = onDismissRequest,
    )
}
