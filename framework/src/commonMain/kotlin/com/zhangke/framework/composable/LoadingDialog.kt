package com.zhangke.framework.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zhangke.fread.framework.Res
import com.zhangke.fread.framework.cancel
import org.jetbrains.compose.resources.stringResource

@Composable
fun CancelableLoadingDialog(
    loading: Boolean,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    properties: DialogProperties = DialogProperties(),
) {
    if (loading) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = properties,
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(vertical = 24.dp, horizontal = 64.dp)
                            .size(80.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onClick = onCancelClick,
                        ) {
                            Text(
                                text = stringResource(Res.string.cancel)
                            )
                        }
                    }
                }
            }
        }
    }
}

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
