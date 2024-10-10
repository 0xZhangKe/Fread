package com.zhangke.fread.status.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp

@Composable
@ReadOnlyComposable
expect fun getScreenWidth(): Dp

@Composable
@ReadOnlyComposable
expect fun getScreenHeight(): Dp