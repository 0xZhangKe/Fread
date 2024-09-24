package com.zhangke.fread.feeds.pages.manager.add.mixed

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.IconButtonStyle
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.StyledIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.snackbarHost
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.fread.analytics.AddMixedFeedsElements
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.feeds.Res
import com.zhangke.fread.feeds.add_feeds_page_feeds_empty
import com.zhangke.fread.feeds.add_feeds_page_feeds_name_hint
import com.zhangke.fread.feeds.add_feeds_page_feeds_name_label
import com.zhangke.fread.feeds.add_feeds_page_title
import com.zhangke.fread.feeds.composable.RemovableStatusSource
import com.zhangke.fread.feeds.composable.StatusSourceUiState
import com.zhangke.fread.feeds.pages.manager.add.showAddContentSuccessToast
import com.zhangke.fread.feeds.pages.manager.search.SearchSourceForAddScreen
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.uri.FormalUri
import org.jetbrains.compose.resources.stringResource

/**
 * 添加混合 Feeds 页面
 */
internal class AddMixedFeedsScreen(
    private val statusSource: StatusSource? = null
) : BaseScreen() {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<AddMixedFeedsViewModel, AddMixedFeedsViewModel.Factory> {
            it.create(statusSource)
        }
        val snackbarHostState = rememberSnackbarHostState()
        FeedsManager(
            uiState = viewModel.uiState.collectAsState().value,
            snackbarHostState = snackbarHostState,
            onBackClick = navigator::pop,
            onAddSourceClick = {
                reportClick(AddMixedFeedsElements.ADD_SOURCE)
                navigator.push(SearchSourceForAddScreen())
            },
            onConfirmClick = {
                reportClick(AddMixedFeedsElements.CONFIRM)
                viewModel.onConfirmClick()
            },
            onNameInputValueChanged = viewModel::onSourceNameInput,
            onRemoveSourceClick = {
                reportClick(AddMixedFeedsElements.DELETE)
                viewModel.onRemoveSource(it)
            },
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
        ConsumeFlow(viewModel.addContentSuccessFlow) {
            showAddContentSuccessToast(context)
            navigator.pop()
        }
        val resultNavigator = navigator.navigationResult
        val addedUri by resultNavigator.getResult<FormalUri>(SearchSourceForAddScreen.SCREEN_KEY)
        if (addedUri != null) {
            LaunchedEffect(addedUri) {
                viewModel.onAddSource(addedUri!!)
            }
        }
    }

    @Composable
    private fun FeedsManager(
        uiState: AddMixedFeedsUiState,
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
                    title = stringResource(Res.string.add_feeds_page_title),
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
                            Text(text = stringResource(Res.string.add_feeds_page_feeds_name_label))
                        },
                        placeholder = {
                            Text(text = stringResource(Res.string.add_feeds_page_feeds_name_hint))
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
                        text = stringResource(Res.string.add_feeds_page_feeds_empty),
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
