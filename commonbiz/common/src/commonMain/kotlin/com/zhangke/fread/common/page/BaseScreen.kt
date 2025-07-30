package com.zhangke.fread.common.page

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.voyager.AnimatedScreen
import com.zhangke.framework.voyager.AnimatedScreenContentScope

open class BaseScreen : Screen {

    @Composable
    override fun Content() {
        BaseScreenHookManager.hookList.forEach {
            it.HookContent(this)
        }
    }
}

open class BaseAnimatedScreen : AnimatedScreen() {

    @Composable
    override fun AnimationContent(animatedScreenContentScope: AnimatedScreenContentScope) {
        BaseScreenHookManager.hookList.forEach {
            it.HookContent(this@BaseAnimatedScreen)
        }
    }
}
