package com.zhangke.framework.nav

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class ScreenEventFlow<T> {

    private val channel = Channel<T>(capacity = Channel.BUFFERED)

    val flow: Flow<T> = channel.receiveAsFlow()

    suspend fun emit(value: T) {
        channel.send(value)
    }

    fun tryEmit(value: T): Boolean {
        return channel.trySend(value).isSuccess
    }
}
