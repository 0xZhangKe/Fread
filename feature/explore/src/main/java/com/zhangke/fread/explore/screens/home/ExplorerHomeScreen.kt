package com.zhangke.fread.explore.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.fread.explore.screens.home.tab.ExplorerFeedsTab
import com.zhangke.fread.explore.screens.home.tab.ExplorerFeedsTabType
import com.zhangke.fread.explore.screens.search.bar.ExplorerSearchBar
import com.zhangke.fread.status.account.LoggedAccount

class ExplorerHomeScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = getViewModel<com.zhangke.fread.explore.screens.home.ExplorerHomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        CompositionLocalProvider(
            LocalNavigator provides navigator
        ) {
            ExplorerHomeContent(
                uiState = uiState,
                onAccountSelected = viewModel::onAccountSelected,
            )
        }
    }

    @Composable
    private fun ExplorerHomeContent(
        uiState: com.zhangke.fread.explore.screens.home.ExplorerHomeUiState,
        onAccountSelected: (LoggedAccount) -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                val selectedAccount = uiState.selectedAccount
                ExplorerSearchBar(
                    selectedAccount = uiState.selectedAccount,
                    accountList = uiState.loggedAccountsList,
                    onAccountSelected = onAccountSelected,
                )
                if (uiState.selectedAccount != null) {
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides snackbarHostState
                    ) {
                        val tabs = remember(selectedAccount) {
                            listOf(
                                ExplorerFeedsTab(
                                    type = ExplorerFeedsTabType.STATUS,
                                    role = uiState.role,
                                ),
                                ExplorerFeedsTab(
                                    type = ExplorerFeedsTabType.HASHTAG,
                                    role = uiState.role,
                                ),
                                ExplorerFeedsTab(
                                    type = ExplorerFeedsTabType.USERS,
                                    role = uiState.role,
                                ),
                            )
                        }
                        HorizontalPagerWithTab(
                            tabList = tabs,
                        )
                    }
                }
            }
        }
    }
}
