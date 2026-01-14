package com.zhangke.fread.activitypub.app.internal.screen.explorer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.HorizontalPagerWithTab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform

class ExplorerContainerTab(
    private val locator: PlatformLocator,
    private val platform: BlogPlatform,
) : BaseTab() {

    override val options: TabOptions?
        @Composable get() = null

    @Composable
    override fun Content() {
        super.Content()
        val tabs = remember {
            listOf(
                ExplorerTab(
                    locator = locator,
                    platform = platform,
                    feedsTabType = ExplorerFeedsTabType.STATUS,
                ),
                ExplorerTab(
                    locator = locator,
                    platform = platform,
                    feedsTabType = ExplorerFeedsTabType.HASHTAG,
                ),
                ExplorerTab(
                    locator = locator,
                    platform = platform,
                    feedsTabType = ExplorerFeedsTabType.USERS,
                ),
            )
        }
        HorizontalPagerWithTab(
            tabList = tabs,
            pagerUserScrollEnabled = true,
        )
    }
}
