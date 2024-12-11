package com.zhangke.framework.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LoadingDialog(
    loading: Boolean,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit = {},
) {
    if (loading) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = properties,
        ) {
            Surface(
                modifier = Modifier,
                shape = RoundedCornerShape(16.dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(vertical = 24.dp, horizontal = 64.dp)
                        .size(80.dp)
                )
            }
        }
    }
}
