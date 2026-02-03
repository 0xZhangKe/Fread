package com.zhangke.fread.common.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.ic_not_found_404
import org.jetbrains.compose.resources.vectorResource

@Composable
fun NotFoundContent(
    modifier: Modifier = Modifier,
    message: String = "404 not found",
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = vectorResource(Res.drawable.ic_not_found_404),
            contentDescription = message,
            modifier = Modifier.size(120.dp),
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
