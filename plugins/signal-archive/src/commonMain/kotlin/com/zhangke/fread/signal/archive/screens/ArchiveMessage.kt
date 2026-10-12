package com.zhangke.fread.signal.archive.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ArchiveMessage(
    loading: Boolean = false,
    failed: Boolean = false,
    pageFailed: Boolean = false,
    onRetry: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        when {
            loading -> CircularProgressIndicator()
            failed -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(
                    if (pageFailed) LocalizedString.signalArchiveFeedsLoadFailed
                    else LocalizedString.signalArchiveLoadFailed,
                ))
                TextButton(onClick = onRetry) { Text(stringResource(LocalizedString.retry)) }
            }
            else -> Text(stringResource(LocalizedString.listContentEmptyPlaceholder))
        }
    }
}
