package com.zhangke.framework.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch

interface PagerTab {

    val options: PagerTabOptions?
        @Composable get

    @Composable
    fun Screen.TabContent()
}

data class PagerTabOptions(
    val title: String,
    val icon: Painter? = null
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Screen.HorizontalPagerWithTab(
    tabList: List<PagerTab>,
    initialPage: Int = 0,
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
        UtopiaTabRow(
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
        ) { pageIndex ->
            with(tabList[pageIndex]) {
                TabContent()
            }
        }
    }
}
