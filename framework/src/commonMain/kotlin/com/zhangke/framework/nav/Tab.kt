package com.zhangke.framework.nav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRowDefaults.primaryContainerColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import com.zhangke.framework.composable.FreadTabRow
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.plusTopPadding
import com.zhangke.framework.utils.pxToDp
import com.zhangke.framework.utils.roundToPx
import com.zhangke.framework.utils.toPx
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

interface Tab {

    val options: TabOptions?
        @Composable get

    @Composable
    fun Content()
}

abstract class BaseTab : Tab {

    @Composable
    override fun Content() {

    }
}

data class TabOptions(
    val title: String,
    val icon: Painter? = null
)

@Composable
fun HorizontalPagerWithTab(
    tabList: List<Tab>,
    initialPage: Int = 0,
    pagerUserScrollEnabled: Boolean = true,
    onPageChanged: ((Int) -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(initialPage = initialPage) {
            tabList.size
        }
        if (onPageChanged != null) {
            LaunchedEffect(pagerState.currentPage) {
                onPageChanged(pagerState.currentPage)
            }
        }
        FreadTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
            tabCount = tabList.size,
            tabContent = {
                Text(
                    text = tabList[it].options?.title.orEmpty(),
                    maxLines = 1,
                )
            },
            onTabClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(it)
                }
            }
        )
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            userScrollEnabled = pagerUserScrollEnabled,
        ) { pageIndex ->
            with(tabList[pageIndex]) {
                Content()
            }
        }
    }
}

@Composable
fun ContentPaddingsHorizontalPagerWithTab(
    tabList: List<Tab>,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    blurEnabled: Boolean = true,
    pagerUserScrollEnabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    onPageChanged: ((Int) -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val pagerState = rememberPagerState(initialPage = initialPage) {
        tabList.size
    }
    if (onPageChanged != null) {
        LaunchedEffect(pagerState.currentPage) {
            onPageChanged(pagerState.currentPage)
        }
    }
    val topOffset = LocalContentPadding.current.calculateTopPadding().roundToPx()
    SubcomposeLayout(modifier = modifier) { constraints ->
        val tabRowPlaceable = subcompose("tabRow") {
            FreadTabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage,
                tabCount = tabList.size,
                containerColor = containerColor,
                blurEffectEnabled = blurEnabled,
                tabContent = {
                    Text(
                        text = tabList[it].options?.title.orEmpty(),
                        maxLines = 1,
                    )
                },
                onTabClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(it)
                    }
                },
            )
        }.first().measure(constraints)
        val tabRowHeightDp = tabRowPlaceable.height.pxToDp(density)
        val pagerPlaceable = subcompose("pager") {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                userScrollEnabled = pagerUserScrollEnabled,
            ) { pageIndex ->
                CompositionLocalProvider(
                    LocalContentPadding provides plusTopPadding(tabRowHeightDp)
                ) {
                    with(tabList[pageIndex]) { Content() }
                }
            }
        }.first().measure(constraints)
        layout(constraints.maxWidth, constraints.maxHeight) {
            pagerPlaceable.placeRelative(0, 0)
            tabRowPlaceable.placeRelative(0, topOffset)
        }
    }
}
