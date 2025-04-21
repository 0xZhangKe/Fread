package com.zhangke.fread.analytics

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.common.page.BasePagerTabHook
import com.zhangke.krouter.annotation.Service

@Service(BasePagerTabHook::class)
class AnalyticsTabHook : BasePagerTabHook {

    @Composable
    override fun HookContent(screen: Screen, tab: BasePagerTab) {
        with(tab) {
            TrackingTabEvent()
        }
    }
}
