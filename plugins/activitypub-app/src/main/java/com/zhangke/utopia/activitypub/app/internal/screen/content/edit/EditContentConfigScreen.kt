package com.zhangke.utopia.activitypub.app.internal.screen.content.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import kotlinx.coroutines.flow.Flow

@Destination(EditContentConfigRoute.ROUTE)
class EditContentConfigScreen(
    @Router private val route: String = ""
) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<EditContentConfigViewModel, EditContentConfigViewModel.Factory> {
            it.create(EditContentConfigRoute.parseRoute(route))
        }
        val uiState by viewModel.uiState.collectAsState()
        EditContentConfigScreenContent(
            uiState = uiState,
            snackbarMessageFlow = viewModel.snackbarMessageFlow,
            onBackClick = navigator::pop,
        )
    }

    @Composable
    private fun EditContentConfigScreenContent(
        uiState: EditContentConfigUiState?,
        snackbarMessageFlow: Flow<TextString>,
        onBackClick: () -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        ConsumeSnackbarFlow(snackbarHostState, snackbarMessageFlow)
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            topBar = {
                Toolbar(
                    title = uiState?.config?.configName.orEmpty(),
                    onBackClick = onBackClick,
                )
            }
        ) { innerPaddings ->
            Column(
                modifier = Modifier
                    .padding(innerPaddings)
                    .fillMaxSize()
            ) {
                // 显示中 Feeds
                // 显示中的 Feeds 才会显示在首页
                // 可添加 Feeds
                // 所有的 Feeds 都可以拖动排序，并且可以拖动到显示中/未显示
            }
        }
    }
}
