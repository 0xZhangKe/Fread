package com.zhangke.fread.common.page

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.krouter.KRouter

object BaseScreenHookManager {

    private val _hookList = mutableListOf<BaseScreenHook>()
    val hookList: List<BaseScreenHook> get() = _hookList

    init {
        _hookList.addAll(KRouter.getServices<BaseScreenHook>())
    }
}

interface BaseScreenHook {

    @Composable
    fun HookContent(screen: Screen)
}
