package com.zhangke.utopia.explore.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.utopia.explore.screens.search.bar.ExplorerSearchBar

class ExplorerHomeScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = getViewModel<ExplorerViewModel>()

        ExplorerHomeContent()
    }

    @Composable
    private fun ExplorerHomeContent(
//        uiState: ExplorerUiState,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                ExplorerSearchBar()
            }
        }
    }
}
