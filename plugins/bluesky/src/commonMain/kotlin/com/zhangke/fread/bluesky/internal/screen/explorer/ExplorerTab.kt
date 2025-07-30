package com.zhangke.fread.bluesky.internal.screen.explorer

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.voyager.AnimatedScreenContentScope
import com.zhangke.framework.voyager.CurrentAnimatedScreen
import com.zhangke.fread.bluesky.internal.screen.feeds.explorer.ExplorerFeedsScreen
import com.zhangke.fread.status.model.PlatformLocator

class ExplorerTab(
    private val locator: PlatformLocator,
) : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?,
        animatedScreenContentScope: AnimatedScreenContentScope?,
    ) {
        if (animatedScreenContentScope == null) {
            Navigator(ExplorerFeedsScreen(locator, true))
        } else {
            with(animatedScreenContentScope.sharedTransitionScope) {
                Navigator(
                    screen = ExplorerFeedsScreen(locator, true),
                ) {
                    CurrentAnimatedScreen(it)
                }
            }
        }
    }
}
