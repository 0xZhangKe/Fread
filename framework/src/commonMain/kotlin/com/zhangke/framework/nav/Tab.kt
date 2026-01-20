package com.zhangke.framework.nav

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
import com.zhangke.framework.composable.FreadTabRow
import kotlinx.coroutines.launch

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
