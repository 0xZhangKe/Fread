package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RemainingTextStatus(
    modifier: Modifier,
    maxCount: Int,
    contentLength: Int,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val remainingCount = maxCount - contentLength
        val overLength = remainingCount < 0
        val fontColor = if (overLength) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        }
        Text(
            text = "${maxCount - contentLength}",
            style = MaterialTheme.typography.labelSmall,
            color = fontColor,
        )
        CircularProgressIndicator(
            modifier = Modifier.padding(start = 4.dp)
                .size(20.dp),
            progress = { contentLength / maxCount.toFloat() },
            trackColor = MaterialTheme.colorScheme.secondaryContainer,
            color = fontColor,
        )
    }
}
