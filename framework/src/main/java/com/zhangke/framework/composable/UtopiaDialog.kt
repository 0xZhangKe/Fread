package com.zhangke.framework.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.utopia.framework.R

@Composable
fun UtopiaDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    title: String? = null,
    contentText: String,
    negativeButtonText: String? = null,
    positiveButtonText: String? = null,
    onNegativeClick: (() -> Unit)? = null,
    onPositiveClick: (() -> Unit)? = null,
) {
    UtopiaDialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
        title = title,
        content = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                val textStyle = MaterialTheme.typography.bodyLarge
                Text(text = contentText, style = textStyle)
            }
        },
        negativeButtonText = negativeButtonText,
        positiveButtonText = positiveButtonText,
        onNegativeClick = onNegativeClick,
        onPositiveClick = onPositiveClick,
    )
}

@Composable
fun UtopiaDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    title: String? = null,
    content: (@Composable () -> Unit)? = null,
    negativeButtonText: String? = null,
    positiveButtonText: String? = null,
    onNegativeClick: (() -> Unit)? = null,
    onPositiveClick: (() -> Unit)? = null,
) {
    UtopiaDialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
        header = if (title.isNullOrEmpty()) {
            null
        } else {
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    val textStyle = MaterialTheme.typography.titleLarge
                    Text(
                        text = title,
                        style = textStyle,
                    )
                }
            }
        },
        content = content,
        negativeButton = if (negativeButtonText.isNullOrEmpty() && onNegativeClick == null) {
            null
        } else {
            {
                TextButton(onClick = { onNegativeClick?.invoke() }) {
                    Text(text = negativeButtonText.ifNullOrEmpty { stringResource(R.string.cancel) })
                }
            }
        },
        positiveButton = if (positiveButtonText.isNullOrEmpty() && onPositiveClick == null) {
            null
        } else {
            {
                TextButton(onClick = { onPositiveClick?.invoke() }) {
                    Text(text = positiveButtonText.ifNullOrEmpty { stringResource(R.string.ok) })
                }
            }
        }
    )
}

@Composable
fun UtopiaDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    header: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
    negativeButton: (@Composable () -> Unit)? = null,
    positiveButton: (@Composable () -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                header?.invoke()
                content?.invoke()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    negativeButton?.invoke()
                    if (positiveButton != null) {
                        Box(modifier = Modifier.size(width = 10.dp, height = 1.dp))
                        positiveButton()
                    }
                }
            }
        }
    }
}
