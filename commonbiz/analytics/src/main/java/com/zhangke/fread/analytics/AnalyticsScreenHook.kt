package com.zhangke.fread.analytics

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.google.auto.service.AutoService
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.common.page.BaseScreenHook

@AutoService(BaseScreenHook::class)
class AnalyticsScreenHook : BaseScreenHook {

    @Composable
    override fun HookContent(screen: BaseScreen) {
        with(screen) {
            TrackingScreenEvent()
        }
    }
}
