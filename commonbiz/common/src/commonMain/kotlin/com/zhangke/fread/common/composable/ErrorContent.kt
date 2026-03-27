package com.zhangke.fread.common.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.img_error_state
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

enum class ErrorType {

    Unknown,
    Network,
    NotFound,
}

@Composable
fun ErrorContent(
    modifier: Modifier,
    errorMessage: String?,
    onRetryClick: () -> Unit,
    type: ErrorType = ErrorType.Unknown,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentScale = ContentScale.Inside,
            painter = painterResource(Res.drawable.img_error_state),
            contentDescription = null,
        )

        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            text = stringResource(
                when (type) {
                    ErrorType.Unknown -> LocalizedString.unknownError
                    ErrorType.Network -> LocalizedString.networkError
                    ErrorType.NotFound -> LocalizedString.resourceNotFound
                }
            ),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )

        if (!errorMessage.isNullOrEmpty()) {
            Text(
                modifier = Modifier.padding(start = 32.dp, top = 8.dp, end = 32.dp),
                text = errorMessage,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetryClick) {
            Text(text = stringResource(LocalizedString.retry))
        }
    }
}
