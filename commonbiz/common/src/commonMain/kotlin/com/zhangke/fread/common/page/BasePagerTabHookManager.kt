package com.zhangke.fread.common.page

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object BasePagerTabHookManager {

    private val _hookList = mutableListOf<BasePagerTabHook>()
    val hookList: List<BasePagerTabHook> get() = _hookList

    init {
        _hookList.addAll(findBasePagerTabImplementers())
    }
}

interface BasePagerTabHook {

    @Composable
    fun HookContent(screen: Screen, tab: BasePagerTab)
}

internal expect fun findBasePagerTabImplementers(): List<BasePagerTabHook>