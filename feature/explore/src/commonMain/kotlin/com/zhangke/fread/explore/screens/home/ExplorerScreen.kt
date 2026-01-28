package com.zhangke.fread.explore.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.updateTopPadding
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
    var topBarHeight: Dp by remember { mutableStateOf(40.dp) }
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CompositionLocalProvider(
            LocalContentPadding provides updateTopPadding(topBarHeight)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
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
        ExplorerSearchBar(
            selectedAccount = uiState.selectedAccount,
            accountList = uiState.accountWithTabList.map { it.first },
            onAccountSelected = onAccountSelected,
            onHeightChanged = { topBarHeight = it },
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = LocalContentPadding.current.calculateBottomPadding() + 16.dp),
        )
    }
}
