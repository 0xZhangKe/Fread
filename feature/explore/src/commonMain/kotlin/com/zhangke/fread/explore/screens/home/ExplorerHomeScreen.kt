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
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.AnimatedScreenContentScope
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.fread.common.page.BaseAnimatedScreen
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.explore.screens.search.bar.ExplorerSearchBar
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NestedTabConnection

class ExplorerHomeScreen : BaseAnimatedScreen() {

    @Composable
    override fun AnimationContent(animatedScreenContentScope: AnimatedScreenContentScope) {
        super.AnimationContent(animatedScreenContentScope)
        val viewModel = getViewModel<ExplorerHomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        CompositionLocalProvider(LocalNavigator provides navigator) {
            ExplorerHomeContent(
                uiState = uiState,
                animatedScreenContentScope = animatedScreenContentScope,
                onAccountSelected = {
                    viewModel.onAccountSelected(it)
                },
            )
        }
    }

    @Composable
    private fun ExplorerHomeContent(
        uiState: ExplorerHomeUiState,
        onAccountSelected: (LoggedAccount) -> Unit,
        animatedScreenContentScope: AnimatedScreenContentScope,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 60.dp),
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                ExplorerSearchBar(
                    selectedAccount = uiState.selectedAccount,
                    accountList = uiState.loggedAccountsList,
                    onAccountSelected = onAccountSelected,
                )
                if (uiState.tab != null) {
                    val nestedTabConnection = remember {
                        NestedTabConnection()
                    }
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides snackbarHostState,
                        LocalNestedTabConnection provides nestedTabConnection,
                    ) {
                        key(uiState.tab) {
                            uiState.tab.TabContent(
                                this@ExplorerHomeScreen,
                                null,
                                animatedScreenContentScope,
                            )
                        }
                    }
                }
            }
        }
    }
}
