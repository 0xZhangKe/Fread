package com.zhangke.fread.screen

import androidx.compose.runtime.Composable
import com.zhangke.framework.voyager.AnimatedScreenContentScope
import com.zhangke.fread.common.page.BaseAnimatedScreen
import com.zhangke.fread.screen.main.MainPage

class FreadScreen : BaseAnimatedScreen() {

    @Composable
    override fun AnimationContent(animatedScreenContentScope: AnimatedScreenContentScope) {
        super.AnimationContent(animatedScreenContentScope)
        MainPage(animatedScreenContentScope)
    }
}
