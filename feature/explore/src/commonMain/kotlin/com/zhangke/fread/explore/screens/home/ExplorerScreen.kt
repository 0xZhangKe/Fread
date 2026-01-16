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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.explore.screens.search.bar.ExplorerSearchBar
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NestedTabConnection
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExplorerScreen() {
    val viewModel = koinViewModel<ExplorerHomeViewModel>()
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
        topBar = {
            ExplorerSearchBar(
                selectedAccount = uiState.selectedAccount,
                accountList = uiState.accountWithTabList.map { it.first },
                onAccountSelected = onAccountSelected,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 60.dp),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.Companion
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            key(uiState.tab) {
                uiState.tab?.let { tab ->
                    val nestedTabConnection = remember { NestedTabConnection() }
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides snackbarHostState,
                        LocalNestedTabConnection provides nestedTabConnection,
                    ) {
                        tab.Content()
                    }
                }
            }
        }
    }
}
