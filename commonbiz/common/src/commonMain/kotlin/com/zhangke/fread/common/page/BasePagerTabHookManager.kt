package com.zhangke.fread.common.page

import androidx.compose.runtime.Composable

object BasePagerTabHookManager {

    private val _hookList = mutableListOf<BasePagerTabHook>()
    val hookList: List<BasePagerTabHook> get() = _hookList

    init {
        _hookList.addAll(findBasePagerTabImplementers())
    }
}

interface BasePagerTabHook {

    @Composable
    fun HookContent()
}

internal expect fun findBasePagerTabImplementers(): List<BasePagerTabHook>