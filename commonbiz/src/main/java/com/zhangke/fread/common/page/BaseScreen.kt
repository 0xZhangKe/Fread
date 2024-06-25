package com.zhangke.fread.common.page

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

open class BaseScreen : Screen {

    @Composable
    override fun Content() {
        BaseScreenHookManager.hookList.forEach {
            it.HookContent(this)
        }
    }
}
