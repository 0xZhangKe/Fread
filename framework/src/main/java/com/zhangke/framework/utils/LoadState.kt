package com.zhangke.framework.utils


sealed interface LoadState {

    val loading: Boolean
        get() = this is Loading

    data object Idle : LoadState

    data object Loading : LoadState

    data class Failed(val e: Throwable) : LoadState
}
