package com.zhangke.fread.common.page

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.voyager.AnimatedScreenContentScope

abstract class BasePagerTab : PagerTab {

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?,
        animatedScreenContentScope: AnimatedScreenContentScope?,
    ) {
        BasePagerTabHookManager.hookList.forEach {
            it.HookContent(screen, this@BasePagerTab)
        }
    }
}
