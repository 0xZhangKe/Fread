package com.zhangke.framework.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

fun Job.invokeOnCancel(block: (CancellationException) -> Unit) {
    invokeOnCompletion {
        if (it is CancellationException) {
            block(it)
        }
    }
}
