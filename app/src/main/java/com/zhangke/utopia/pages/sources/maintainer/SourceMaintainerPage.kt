package com.zhangke.utopia.pages.sources.maintainer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.utopia.composable.ObserveSnackbar
import com.zhangke.utopia.composable.Toolbar
import com.zhangke.utopia.composable.rememberSnackbarHostState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceMaintainerPage(
    uiState: SourceMaintainerUiState,
) {
    val snackbarHostState = rememberSnackbarHostState()
    ObserveSnackbar(snackbarHostState, uiState.errorMessageText)
    Scaffold(
        topBar = {
            Toolbar(title = uiState.title)
        }
    ) {
        LoadableLayout(
            modifier = Modifier.padding(it),
            state = uiState.maintainerState,
        ) { maintainerState ->

        }
    }
}
