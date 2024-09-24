package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun WebViewPreviewer(
    html: String,
    modifier: Modifier = Modifier,
)
