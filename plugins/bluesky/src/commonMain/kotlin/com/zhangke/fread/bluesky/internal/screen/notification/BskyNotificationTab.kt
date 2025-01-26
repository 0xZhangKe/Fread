package com.zhangke.fread.bluesky.internal.screen.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions

class BskyNotificationTab : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?
    ) {
        TODO("Not yet implemented")
    }
}
