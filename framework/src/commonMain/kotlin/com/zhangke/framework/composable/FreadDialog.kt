package com.zhangke.framework.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.fread.framework.Res
import com.zhangke.fread.framework.alert
import com.zhangke.fread.framework.cancel
import com.zhangke.fread.framework.ok
import org.jetbrains.compose.resources.stringResource

@Composable
fun FreadDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    title: String? = null,
    contentText: String,
    negativeButtonText: String? = null,
    positiveButtonText: String? = null,
    onNegativeClick: (() -> Unit)? = null,
    onPositiveClick: (() -> Unit)? = null,
) {
    FreadDialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
        title = title,
        content = {
            Text(text = contentText)
        },
        negativeButtonText = negativeButtonText,
        positiveButtonText = positiveButtonText,
        onNegativeClick = onNegativeClick,
        onPositiveClick = onPositiveClick,
    )
}

@Composable
fun FreadDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    title: String? = null,
    content: (@Composable () -> Unit)? = null,
    negativeButtonText: String? = null,
    positiveButtonText: String? = null,
    onNegativeClick: (() -> Unit)? = null,
    onPositiveClick: (() -> Unit)? = null,
) {
    FreadDialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
        header = {
            Text(
                text = title.ifNullOrEmpty { stringResource(Res.string.alert) },
            )
        },
        content = content,
        negativeButton = if (negativeButtonText.isNullOrEmpty() && onNegativeClick == null) {
            null
        } else {
            {
                TextButton(onClick = { onNegativeClick?.invoke() }) {
                    Text(text = negativeButtonText.ifNullOrEmpty { stringResource(Res.string.cancel) })
                }
            }
        },
        positiveButton = if (positiveButtonText.isNullOrEmpty() && onPositiveClick == null) {
            null
        } else {
            {
                TextButton(onClick = { onPositiveClick?.invoke() }) {
                    Text(text = positiveButtonText.ifNullOrEmpty { stringResource(Res.string.ok) })
                }
            }
        }
    )
}

@Composable
fun FreadDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    header: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
    titleContentColor: Color = MaterialTheme.colorScheme.onSurface,
    textContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 24.dp, top = 24.dp, end = 20.dp, bottom = 12.dp)
            ) {
                if (header != null) {
                    ProvideContentColorTextStyle(
                        contentColor = titleContentColor,
                        textStyle = MaterialTheme.typography.headlineSmall,
                    ) {
                        Box(
                            Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            header()
                        }
                    }
                }
                if (content != null) {
                    val textStyle = MaterialTheme.typography.bodyMedium
                    ProvideContentColorTextStyle(
                        contentColor = textContentColor,
                        textStyle = textStyle
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .align(Alignment.Start),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            content()
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    negativeButton?.invoke()
                    if (positiveButton != null) {
                        Spacer(modifier = Modifier.size(width = 8.dp, height = 1.dp))
                        positiveButton()
                    }
                }
            }
        }
    }
}

@Composable
private fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    content: @Composable () -> Unit
) {
    val mergedStyle = LocalTextStyle.current.merge(textStyle)
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides mergedStyle,
        content = content
    )
}
