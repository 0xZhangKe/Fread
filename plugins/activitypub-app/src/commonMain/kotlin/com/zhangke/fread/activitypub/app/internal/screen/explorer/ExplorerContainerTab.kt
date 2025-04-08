package com.zhangke.fread.activitypub.app.internal.screen.explorer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform

class ExplorerContainerTab(
    private val role: IdentityRole,
    private val platform: BlogPlatform,
) : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        val tabs = remember {
            listOf(
                ExplorerTab(
                    role = role,
                    platform = platform,
                    feedsTabType = ExplorerFeedsTabType.STATUS,
                ),
                ExplorerTab(
                    role = role,
                    platform = platform,
                    feedsTabType = ExplorerFeedsTabType.HASHTAG,
                ),
                ExplorerTab(
                    role = role,
                    platform = platform,
                    feedsTabType = ExplorerFeedsTabType.USERS,
                ),
            )
        }
        with(screen) {
            HorizontalPagerWithTab(
                tabList = tabs,
                pagerUserScrollEnabled = true,
            )
        }
    }
}
