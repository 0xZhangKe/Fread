package com.zhangke.framework.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val lineHeight = 68.dp

@Composable
fun LoadingLineItem(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.height(lineHeight),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun LoadErrorLineItem(
    modifier: Modifier,
    errorMessage: TextString,
) {
    LoadErrorLineItem(
        modifier = modifier,
        errorMessage = textString(text = errorMessage),
    )
}

@Composable
fun LoadErrorLineItem(
    modifier: Modifier,
    errorMessage: String,
) {
    Box(
        modifier = modifier.heightIn(min = lineHeight),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            textAlign = TextAlign.Center,
            text = errorMessage,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
fun PreviewShortLoadErrorLineItem(){
    LoadErrorLineItem(
        modifier = Modifier.fillMaxWidth(),
        errorMessage = "123",
    )
}

@Preview
@Composable
fun PreviewLongLoadErrorLineItem(){
    LoadErrorLineItem(
        modifier = Modifier.fillMaxWidth(),
        errorMessage = "123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123123",
    )
}