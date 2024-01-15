package com.zhangke.utopia.feeds.pages.manager.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.composable.successDataOrNull
import com.zhangke.utopia.feeds.composable.RemovableStatusSource
import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.feeds.pages.manager.search.SearchSourceForAddScreen

class EditFeedsScreen(private val feedsId: Long) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: EditFeedsViewModel = getViewModel()
        viewModel.feedsId = feedsId
        LaunchedEffect(feedsId) {
            viewModel.onPageResume()
        }
        val uiState by viewModel.uiState.collectAsState()
        EditFeedsScreenContent(
            uiState = uiState,
            onRemoveSourceClick = viewModel::onSourceDelete,
            onEditNameClick = viewModel::onEditName,
            onBackClick = navigator::pop,
            onAddSourceClick = {
                navigator.push(
                    SearchSourceForAddScreen(
                        onUrisAdded = viewModel::onAddSources
                    )
                )
            },
            onDeleteClick = viewModel::onDeleteFeeds,
        )
        ConsumeFlow(viewModel.finishScreenFlow) {
            navigator.pop()
        }
    }

    @Composable
    private fun EditFeedsScreenContent(
        uiState: LoadableState<EditFeedsUiState>,
        onRemoveSourceClick: (StatusSourceUiState) -> Unit,
        onEditNameClick: (String) -> Unit,
        onBackClick: () -> Unit,
        onAddSourceClick: () -> Unit,
        onDeleteClick: () -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        val errorMessage = uiState.successDataOrNull()?.errorMessage
        if (errorMessage.isNullOrEmpty().not()) {
            LaunchedEffect(errorMessage) {
                snackbarHostState.showSnackbar(errorMessage.orEmpty())
            }
        }
        Scaffold(
            topBar = {
                EditFeedsScreenTopBar(
                    uiState = uiState,
                    onEditNameClick = onEditNameClick,
                    onBackClick = onBackClick,
                    onDeleteClick = onDeleteClick,
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddSourceClick) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Default.Add),
                        contentDescription = "Add Source",
                    )
                }
            }
        ) { paddings ->
            LoadableLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings),
                state = uiState,
            ) { uiState ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.sourceList) { item ->
                        RemovableStatusSource(
                            modifier = Modifier.fillMaxWidth(),
                            source = item,
                            onRemoveClick = {
                                onRemoveSourceClick(item)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditFeedsScreenTopBar(
    uiState: LoadableState<EditFeedsUiState>,
    onEditNameClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var showEditNameDialog by remember {
        mutableStateOf(false)
    }
    var showDeleteConfirmDialog by remember {
        mutableStateOf(false)
    }
    Toolbar(
        title = uiState.successDataOrNull()?.name.orEmpty(),
        onBackClick = onBackClick,
        actions = {
            IconButton(
                onClick = {
                    showEditNameDialog = true
                },
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.Edit),
                    contentDescription = "Edit Name",
                )
            }

            IconButton(
                onClick = {
                    showDeleteConfirmDialog = true
                },
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.Delete),
                    contentDescription = "Delete Feeds",
                )
            }
        },
    )
    var inputtedText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(uiState) {
        if (inputtedText.isEmpty() && uiState.isSuccess) {
            inputtedText = uiState.requireSuccessData().name
        }
    }
    if (showEditNameDialog) {
        Dialog(
            onDismissRequest = { showEditNameDialog = false },
        ) {
            Surface(
                elevation = 6.dp,
                shape = RoundedCornerShape(4.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp)) {
                    Text(
                        text = "Input Feeds Name",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        value = inputtedText,
                        onValueChange = { inputtedText = it },
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 6.dp)
                    ) {
                        TextButton(onClick = { showEditNameDialog = false }) {
                            Text(text = stringResource(com.zhangke.utopia.framework.R.string.cancel))
                        }
                        TextButton(
                            modifier = Modifier.padding(start = 6.dp),
                            onClick = {
                                showEditNameDialog = false
                                onEditNameClick(inputtedText)
                            }
                        ) {
                            Text(text = stringResource(com.zhangke.utopia.framework.R.string.ok))
                        }
                    }
                }
            }
        }
    }
    if (showDeleteConfirmDialog) {
        Dialog(onDismissRequest = { showDeleteConfirmDialog = false }) {
            Surface(
                elevation = 6.dp,
                shape = RoundedCornerShape(4.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "Input Feeds Name",
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 6.dp)
                    ) {
                        TextButton(onClick = { showDeleteConfirmDialog = false }) {
                            Text(text = stringResource(com.zhangke.utopia.framework.R.string.cancel))
                        }
                        TextButton(
                            modifier = Modifier.padding(start = 6.dp),
                            onClick = {
                                showDeleteConfirmDialog = false
                                onDeleteClick()
                            }
                        ) {
                            Text(text = stringResource(com.zhangke.utopia.framework.R.string.ok))
                        }
                    }
                }
            }
        }
    }
}
