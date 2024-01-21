package com.zhangke.utopia.feature.message.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.UtopiaTabRow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.utopia.feature.notifications.R
import kotlinx.coroutines.launch

class NotificationsHomeScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: NotificationsHomeViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        NotificationsHomeScreenContent(
            uiState = uiState,
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun NotificationsHomeScreenContent(
        uiState: NotificationsHomeUiState,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val snackbarHost = rememberSnackbarHostState()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(snackbarHost)
            }
        ) { paddings ->
            val accountToTabList = uiState.accountToTabList
            if (accountToTabList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(paddings)
                        .fillMaxSize()
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.notifications_account_empty_tip),
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    val pagerState = rememberPagerState {
                        accountToTabList.size
                    }
                    UtopiaTabRow(
                        modifier = Modifier.fillMaxWidth(),
                        selectedTabIndex = pagerState.currentPage,
                        tabCount = accountToTabList.size,
                        tabContent = {
                            var title = accountToTabList[it].second.options?.title
                            if (title.isNullOrEmpty()) {
                                title = accountToTabList[it].first.userName
                            }
                            Text(
                                text = title,
                                maxLines = 1,
                            )
                        },
                        onTabClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(it)
                            }
                        }
                    )
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides snackbarHost,
                    ) {
                        HorizontalPager(
                            modifier = Modifier.fillMaxSize(),
                            state = pagerState,
                        ) { pageIndex ->
                            with(accountToTabList[pageIndex].second) {
                                TabContent()
                            }
                        }
                    }
                }
            }
        }
    }
}
