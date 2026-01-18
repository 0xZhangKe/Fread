package com.zhangke.fread.common.utils

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object GlobalScreenNavigation {

    private val _openScreenFlow = MutableSharedFlow<NavKey>()
    val openScreenFlow: SharedFlow<NavKey> get() = _openScreenFlow.asSharedFlow()

    private val _openTransparentScreenFlow = MutableSharedFlow<NavKey>()
    val openTransparentScreenFlow: SharedFlow<NavKey> get() = _openTransparentScreenFlow.asSharedFlow()

    suspend fun navigate(screen: NavKey) {
        _openScreenFlow.emit(screen)
    }

    suspend fun navigateByTransparent(screen: NavKey) {
        _openTransparentScreenFlow.emit(screen)
    }
}
