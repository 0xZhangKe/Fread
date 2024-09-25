package com.zhangke.fread.activitypub.app.internal.screen.status.post.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zhangke.framework.architect.theme.inverseOnSurfaceDark
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.post_status_content_warning
import com.zhangke.fread.status.ui.drawSpoilerBackground
import org.jetbrains.compose.resources.stringResource

@Composable
fun PostStatusWarning(
    modifier: Modifier,
    warning: String?,
    onValueChanged: (String) -> Unit,
) {
    Box(
        modifier = modifier.drawSpoilerBackground()
    ) {
        TextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize(),
            value = warning.orEmpty(),
            onValueChange = onValueChanged,
            placeholder = {
                Text(
                    text = stringResource(Res.string.post_status_content_warning),
                    style = MaterialTheme.typography.bodyMedium,
                    color = inverseOnSurfaceDark,
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = inverseOnSurfaceDark,
                unfocusedTextColor = inverseOnSurfaceDark,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
        )
    }
}
