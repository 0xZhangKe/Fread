package com.zhangke.framework.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

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