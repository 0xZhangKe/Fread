package com.zhangke.fread.bluesky.internal.screen.explorer

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.bluesky.internal.screen.feeds.explorer.ExplorerFeedsScreen
import com.zhangke.fread.status.model.PlatformLocator

class ExplorerTab(
    private val locator: PlatformLocator,
) : BaseTab() {

    override val options: TabOptions?
        @Composable get() = null

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    override fun Content() {
        super.Content()
        ExplorerFeedsScreen(locator, true)
    }
}
