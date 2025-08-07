package com.zhangke.fread.explore.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.ic_logo_small
import com.zhangke.fread.explore.screens.search.bar.ExplorerSearchBar
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NestedTabConnection
import org.jetbrains.compose.resources.painterResource

class ExploreTab() : PagerTab {

    override val options: PagerTabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_logo_small)
            return remember {
                PagerTabOptions(
                    title = "Explore", icon = icon
                )
            }
        }

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?,
    ) {
        val viewModel = screen.getViewModel<ExplorerHomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        screen.ExplorerHomeContent(
            uiState = uiState,
            onAccountSelected = viewModel::onAccountSelected,
        )
    }

    @Composable
    private fun Screen.ExplorerHomeContent(
        uiState: ExplorerHomeUiState,
        onAccountSelected: (LoggedAccount) -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.Companion.padding(bottom = 60.dp),
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.Companion
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                ExplorerSearchBar(
                    selectedAccount = uiState.selectedAccount,
                    accountList = uiState.accountWithTabList.map { it.first },
                    onAccountSelected = onAccountSelected,
                )
//                val pagerState = rememberPagerState { uiState.accountWithTabList.size }
//                HorizontalPager(
//                    state = pagerState,
//                    userScrollEnabled = false,
//                ){
//
//                }
                uiState.tab?.let { tab ->
                    val nestedTabConnection = remember { NestedTabConnection() }
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides snackbarHostState,
                        LocalNestedTabConnection provides nestedTabConnection,
                    ) {
                        tab.TabContent(
                            this@ExplorerHomeContent,
                            null,
                        )
                    }
                }
            }
        }
    }
}
