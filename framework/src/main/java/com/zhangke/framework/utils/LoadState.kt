package com.zhangke.framework.utils

import com.zhangke.framework.composable.TextString


sealed interface LoadState {

    val loading: Boolean
        get() = this is Loading

    data object Idle : LoadState

    data object Loading : LoadState

    data class Failed(val message: TextString?) : LoadState
}
