package com.zhangke.fread.bluesky.internal.screen.explorer

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.bluesky.internal.screen.feeds.explorer.ExplorerFeedsScreen
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform

class ExplorerTab(
    private val role: IdentityRole,
    private val blogPlatform: BlogPlatform,
) : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        Navigator(ExplorerFeedsScreen(role, true))
    }
}
