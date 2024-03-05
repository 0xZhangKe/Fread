package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun <T> ConsumeFlow(
    flow: Flow<T>,
    block: suspend (T) -> Unit
) {
    LaunchedEffect(flow) {
        flow.collect {
            block(it)
        }
    }
}

suspend fun MutableSharedFlow<TextString>.tryEmitException(exception: Throwable) {
    exception.message
        ?.takeIf { it.isNotEmpty() }
        ?.let { textOf(it) }
        ?.let {
            this.emit(it)
        }
}
