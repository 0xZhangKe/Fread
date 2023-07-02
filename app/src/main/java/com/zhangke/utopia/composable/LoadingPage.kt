package com.zhangke.utopia.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoadingPage(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(0.5F)
        )
    }
}

@Preview
@Composable
private fun PreviewLoadingPage() {
    LoadingPage(modifier = Modifier.fillMaxSize())
}
