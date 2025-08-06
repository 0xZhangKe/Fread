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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.rootNavigator
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
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        CompositionLocalProvider(LocalNavigator provides navigator) {
            screen.ExplorerHomeContent(
                uiState = uiState,
                onAccountSelected = {
                    viewModel.onAccountSelected(it)
                },
            )
        }
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
                                this@ExplorerHomeContent,
                                null,
                            )
                        }
                    }
                }
            }
        }
    }
}