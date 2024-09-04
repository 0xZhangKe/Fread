package com.zhangke.fread.common.page

import androidx.compose.runtime.Composable

object BaseScreenHookManager {

    private val _hookList = mutableListOf<BaseScreenHook>()
    val hookList: List<BaseScreenHook> get() = _hookList

    init {
        _hookList.addAll(findBaseScreenHookImplementers())
    }
}

interface BaseScreenHook {

    @Composable
    fun HookContent(screen: BaseScreen)
}


internal expect fun findBaseScreenHookImplementers(): List<BaseScreenHook>