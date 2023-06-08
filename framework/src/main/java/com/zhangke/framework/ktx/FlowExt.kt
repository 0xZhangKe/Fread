package com.zhangke.framework.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> Flow<T>.CollectOnComposable(block: (T) -> Unit) {
    LaunchedEffect(this) {
        collect {
            block(it)
        }
    }
}
