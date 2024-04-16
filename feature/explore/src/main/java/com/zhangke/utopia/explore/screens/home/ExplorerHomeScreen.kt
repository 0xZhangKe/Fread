package com.zhangke.utopia.explore.screens.home

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
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.utopia.explore.screens.home.tab.ExplorerFeedsTab
import com.zhangke.utopia.explore.screens.home.tab.ExplorerFeedsTabType
import com.zhangke.utopia.explore.screens.search.bar.ExplorerSearchBar
import com.zhangke.utopia.status.account.LoggedAccount

class ExplorerHomeScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = getViewModel<ExplorerHomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        ExplorerHomeContent(
            uiState = uiState,
            onAccountSelected = viewModel::onAccountSelected,
        )
    }

    @Composable
    private fun ExplorerHomeContent(
        uiState: ExplorerHomeUiState,
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
                if (uiState.role != null) {
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
