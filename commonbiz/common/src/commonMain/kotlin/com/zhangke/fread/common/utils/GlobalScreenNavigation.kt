package com.zhangke.fread.common.utils

import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object GlobalScreenNavigation {

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow: SharedFlow<Screen> get() = _openScreenFlow.asSharedFlow()

    private val _openTransparentScreenFlow = MutableSharedFlow<Screen>()
    val openTransparentScreenFlow: SharedFlow<Screen> get() = _openTransparentScreenFlow.asSharedFlow()

    suspend fun navigate(screen: Screen) {
        _openScreenFlow.emit(screen)
    }

    suspend fun navigateByTransparent(screen: Screen){
        _openTransparentScreenFlow.emit(screen)
    }
}
