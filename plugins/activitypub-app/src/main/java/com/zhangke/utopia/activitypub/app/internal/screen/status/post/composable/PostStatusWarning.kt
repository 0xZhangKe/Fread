package com.zhangke.utopia.activitypub.app.internal.screen.status.post.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.activitypub.app.R

@Composable
fun PostStatusWarning(
    modifier: Modifier,
    warning: String?,
    onValueChanged: (String) -> Unit,
) {
    Box(
        modifier = modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.error,
            shape = RoundedCornerShape(4.dp),
        )
    ) {
        TextField(
            modifier = Modifier.fillMaxSize(),
            value = warning.orEmpty(),
            onValueChange = onValueChanged,
            placeholder = {
                Text(
                    text = stringResource(R.string.post_status_content_warning),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
        )
    }
}
