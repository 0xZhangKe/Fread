package com.zhangke.utopia.pages.feeds.container

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.utopia.pages.feeds.FeedsPage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedsContainerPage(
    uiState: FeedsContainerUiState,
    onTabSelected: (Int) -> Unit,
    onAddFeedsClick: () -> Unit,
    onRefresh: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                modifier = Modifier.padding(end = 15.dp),
                onClick = onAddFeedsClick,
            ) {
                Text(text = "Add Feeds")
            }
            TextButton(
                onClick = onRefresh,
            ) {
                Text(text = "Refresh")
            }
        }
        LoadableLayout(state = uiState.pageUiStateList) { feedsList ->
            if (feedsList.isNotEmpty()) {
                TabRow(
                    modifier = Modifier
                        .height(58.dp)
                        .fillMaxWidth(),
                    selectedTabIndex = uiState.tabIndex,
                ) {
                    feedsList.forEachIndexed { index, item ->
                        Tab(
                            selected = uiState.tabIndex == index,
                            onClick = { onTabSelected(index) },
                        ) {
                            Text(
                                text = item.name,
                            )
                        }
                    }
                }
                val pagerState = rememberPagerState(uiState.tabIndex)
                var currentPageIndex by remember {
                    mutableStateOf(uiState.tabIndex)
                }
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                    pageCount = feedsList.size,
                    userScrollEnabled = true,
                ) { currentPage ->
                    if (currentPageIndex != currentPage) {
                        currentPageIndex = currentPage
                        LaunchedEffect(currentPageIndex) {
                            onTabSelected(currentPageIndex)
                        }
                    }
                    FeedsPage(uiState = feedsList[currentPage])
                }
            } else {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = onAddFeedsClick,
                ) {
                    Text(text = "Add Feeds")
                }
            }
        }
    }
}
