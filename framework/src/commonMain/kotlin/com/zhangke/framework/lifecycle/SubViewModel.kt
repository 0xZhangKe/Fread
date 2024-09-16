package com.zhangke.framework.lifecycle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

import kotlin.coroutines.CoroutineContext

abstract class SubViewModel : AutoCloseable, CoroutineScope {

    final override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    val viewModelScope = CoroutineScope(coroutineContext)

    override fun close() {
        coroutineContext.cancel()
    }
}
