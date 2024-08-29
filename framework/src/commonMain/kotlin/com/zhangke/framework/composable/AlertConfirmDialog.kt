package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import com.zhangke.fread.framework.Res
import com.zhangke.fread.framework.alert
import com.zhangke.fread.framework.cancel
import com.zhangke.fread.framework.ok
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
