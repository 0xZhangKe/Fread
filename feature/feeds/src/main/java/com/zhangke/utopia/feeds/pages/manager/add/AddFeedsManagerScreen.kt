package com.zhangke.utopia.feeds.pages.manager.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.snackbarHost
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.krouter.Destination
import com.zhangke.utopia.commonbiz.shared.router.SharedRouter
import com.zhangke.utopia.commonbiz.shared.screen.login.LoginBottomSheetScreen
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.feeds.composable.RemovableStatusSource
import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.feeds.pages.manager.search.SearchSourceForAddScreen
import kotlinx.coroutines.flow.Flow

@Destination(SharedRouter.Feeds.add)
internal class AddFeedsManagerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val navigationResult = navigator.navigationResult
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel: AddFeedsManagerViewModel = getViewModel()
        FeedsManager(
            uiState = viewModel.uiState.collectAsState().value,
            errorMessageFlow = viewModel.errorMessageFlow,
            onAddSourceClick = {
                navigator.push(
                    SearchSourceForAddScreen(
                        onUrisAdded = viewModel::onAddSources
                    )
                )
            },
            onConfirmClick = viewModel::onConfirmClick,
            onNameInputValueChanged = viewModel::onSourceNameInput,
            onRemoveSourceClick = viewModel::onRemoveSource,
        )
        ConsumeFlow(viewModel.contentConfigFlow) {
            navigationResult.popWithResult(it)
        }
        ConsumeFlow(viewModel.loginRecommendPlatform) {
            bottomSheetNavigator.show(LoginBottomSheetScreen(it))
        }
    }

    @Composable
    private fun FeedsManager(
        uiState: AddFeedsManagerUiState,
        errorMessageFlow: Flow<TextString>,
        onAddSourceClick: () -> Unit,
        onConfirmClick: () -> Unit,
        onNameInputValueChanged: (String) -> Unit,
        onRemoveSourceClick: (item: StatusSourceUiState) -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        ConsumeSnackbarFlow(snackbarHostState, errorMessageFlow)
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(id = R.string.add_feeds_page_title),
                    actions = {
                        IconButton(
                            modifier = Modifier.size(24.dp),
                            onClick = onConfirmClick,
                        ) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Default.PostAdd),
                                contentDescription = "Add",
                            )
                        }
                    }
                )
            },
            snackbarHost = snackbarHost(snackbarHostState),
            floatingActionButton = {
                FloatingActionButton(onClick = onAddSourceClick) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Default.Add),
                        contentDescription = "Add Source",
                    )
                }
            }
        ) { paddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.sourceName,
                        onValueChange = onNameInputValueChanged,
                    )
                }

                LazyColumn(
                    modifier = Modifier.padding(top = 15.dp)
                ) {
                    items(uiState.sourceList) { item ->
                        RemovableStatusSource(
                            modifier = Modifier.fillMaxWidth(),
                            source = item,
                            onRemoveClick = {
                                onRemoveSourceClick(item)
                            },
                        )
                    }
                }
            }
        }
    }
}
