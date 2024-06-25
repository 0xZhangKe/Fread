package com.zhangke.fread.common.page

import androidx.compose.runtime.Composable
import com.zhangke.fread.status.utils.findImplementers

object BaseScreenHookManager {

    private val _hookList = mutableListOf<BaseScreenHook>()
    val hookList: List<BaseScreenHook> get() = _hookList

    init {
        _hookList.addAll(findImplementers<BaseScreenHook>())
    }
}

interface BaseScreenHook {

    @Composable
    fun HookContent(screen: BaseScreen)
}
