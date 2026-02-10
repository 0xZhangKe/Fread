package com.zhangke.fread.activitypub.app.internal.screen.explorer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.FreadTabRow
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.plusTopPadding
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.framework.utils.pxToDp
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform
import kotlinx.coroutines.launch

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
        val density = LocalDensity.current
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
        var tabBarHeight by remember { mutableStateOf(24.dp) }
        CompositionLocalProvider(
            LocalContentPadding provides plusTopPadding(tabBarHeight),
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { pageIndex ->
                with(tabs[pageIndex]) {
                    Content()
                }
            }
        }
        Column {
            Spacer(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = LocalContentPadding.current.calculateTopPadding())
            )
            FreadTabRow(
                modifier = Modifier.fillMaxWidth()
                    .onSizeChanged { tabBarHeight = it.height.pxToDp(density) },
                selectedTabIndex = pagerState.currentPage,
                tabCount = tabs.size,
                tabContent = {
                    Text(
                        text = tabs[it].options.title,
                        maxLines = 1,
                    )
                },
                onTabClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(it)
                    }
                },
            )
        }
    }
}
