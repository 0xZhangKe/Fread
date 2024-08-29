package com.zhangke.framework.composable

import kotlinx.coroutines.flow.MutableSharedFlow

suspend fun MutableSharedFlow<TextString>.tryEmitException(exception: Throwable) {
    exception.message
        ?.takeIf { it.isNotEmpty() }
        ?.let { textOf(it) }
        ?.let {
            this.emit(it)
        }
}
