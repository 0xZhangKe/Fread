package com.zhangke.fread.commonbiz.shared.screen.publish.composable

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.zhangke.framework.architect.theme.inverseOnSurfaceDark
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.ui.drawSpoilerBackground
import org.jetbrains.compose.resources.stringResource

@Composable
fun PostStatusWarning(
    modifier: Modifier,
    warning: TextFieldValue,
    onValueChanged: (TextFieldValue) -> Unit,
) {
    Box(
        modifier = modifier.drawSpoilerBackground()
    ) {
        TextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize(),
            value = warning,
            onValueChange = onValueChanged,
            placeholder = { Text(text = stringResource(LocalizedString.postStatusContentWarning)) },
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
