package com.zhangke.framework.utils


sealed interface LoadState {

    data object Idle : LoadState

    data object Loading : LoadState

    data class Failed(val e: Throwable) : LoadState
}
