package com.zhangke.fread.analytics

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.common.page.BaseScreenHook
import com.zhangke.krouter.annotation.Service

@Service(BaseScreenHook::class)
class AnalyticsScreenHook : BaseScreenHook {

    @Composable
    override fun HookContent(screen: Screen) {
        with(screen) {
            TrackingScreenEvent()
        }
    }
}
