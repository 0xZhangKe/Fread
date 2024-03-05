package com.zhangke.utopia.feeds.pages.manager.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.IconButtonStyle
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.StyledIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.snackbarHost
import com.zhangke.utopia.commonbiz.shared.screen.login.LoginBottomSheetScreen
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.feeds.composable.RemovableStatusSource
import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.feeds.pages.manager.search.SearchSourceForAddScreen
import com.zhangke.utopia.status.source.StatusSource

/**
 * 添加混合 Feeds 页面
 */
internal class AddFeedsManagerScreen(
    private val statusSource: StatusSource? = null
) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel = getViewModel<AddFeedsManagerViewModel, AddFeedsManagerViewModel.Factory> {
            it.create(statusSource)
        }
        val snackbarHostState = rememberSnackbarHostState()
        FeedsManager(
            uiState = viewModel.uiState.collectAsState().value,
            snackbarHostState = snackbarHostState,
            onBackClick = navigator::pop,
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
        ConsumeFlow(viewModel.loginRecommendPlatform) {
            bottomSheetNavigator.show(LoginBottomSheetScreen(it))
        }
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
        ConsumeFlow(viewModel.addContentSuccessFlow) {
            snackbarHostState.showSnackbar(context.getString(R.string.add_content_success_snackbar))
            navigator.pop()
        }
    }

    @Composable
    private fun FeedsManager(
        uiState: AddFeedsManagerUiState,
        snackbarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onAddSourceClick: () -> Unit,
        onConfirmClick: () -> Unit,
        onNameInputValueChanged: (String) -> Unit,
        onRemoveSourceClick: (item: StatusSourceUiState) -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(id = R.string.add_feeds_page_title),
                    onBackClick = onBackClick,
                    actions = {
                        SimpleIconButton(
                            onClick = onConfirmClick,
                            imageVector = Icons.Default.Check,
                            contentDescription = "Add",
                        )
                    }
                )
            },
            snackbarHost = snackbarHost(snackbarHostState),
        ) { paddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
            ) {
                Box(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .weight(1F),
                        value = uiState.sourceName,
                        maxLines = 1,
                        label = {
                            Text(text = stringResource(id = R.string.add_feeds_page_feeds_name_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.add_feeds_page_feeds_name_hint))
                        },
                        onValueChange = {
                            onNameInputValueChanged(it.take(uiState.maxNameLength))
                        },
                    )

                    StyledIconButton(
                        modifier = Modifier.padding(end = 8.dp),
                        imageVector = Icons.Default.Add,
                        style = IconButtonStyle.STANDARD,
                        onClick = onAddSourceClick,
                    )
                }

                if (uiState.sourceList.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                onAddSourceClick()
                            },
                        text = stringResource(id = R.string.add_feeds_page_feeds_empty),
                        style = MaterialTheme.typography.labelLarge,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(top = 32.dp),
                        contentPadding = PaddingValues(bottom = 32.dp),
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
}
