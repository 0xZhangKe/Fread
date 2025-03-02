package com.zhangke.fread.commonbiz.shared.screen.publish.composable

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.framework.utils.transparentColors
import com.zhangke.fread.status.ui.common.PostStatusTextVisualTransformation

@Composable
fun InputBlogTextField(
    modifier: Modifier,
    textFieldValue: TextFieldValue,
    placeholder: String,
    onContentChanged: (TextFieldValue) -> Unit,
) {
    TextField(
        modifier = modifier,
        shape = GenericShape { _, _ -> },
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        minLines = 3,
        visualTransformation = PostStatusTextVisualTransformation(
            highLightColor = MaterialTheme.colorScheme.primary,
        ),
        value = textFieldValue,
        colors = TextFieldDefaults.transparentColors,
        textStyle = MaterialTheme.typography.bodyLarge,
        onValueChange = {
            onContentChanged(it)
        },
    )
}
