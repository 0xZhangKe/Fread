package com.zhangke.fread.feeds.pages.manager.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.successDataOrNull
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.feeds.Res
import com.zhangke.fread.feeds.composable.RemovableStatusSource
import com.zhangke.fread.feeds.composable.StatusSourceUiState
import com.zhangke.fread.feeds.feeds_mixed_config_edit_delete_content_dialog_message
import com.zhangke.fread.feeds.feeds_mixed_config_edit_new_name_dialog_label
import com.zhangke.fread.feeds.feeds_mixed_config_edit_new_name_dialog_title
import com.zhangke.fread.feeds.pages.manager.search.SearchSourceForAddScreen
import com.zhangke.fread.status.uri.FormalUri
import org.jetbrains.compose.resources.stringResource

class EditMixedContentScreen(private val configId: Long) : BaseScreen() {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<EditMixedContentViewModel, EditMixedContentViewModel.Factory> {
            it.create(configId)
        }
        val uiState by viewModel.uiState.collectAsState()
        EditFeedsScreenContent(
            uiState = uiState,
            onRemoveSourceClick = viewModel::onSourceDelete,
            onEditNameClick = viewModel::onEditName,
            onBackClick = navigator::pop,
            onAddSourceClick = {
                navigator.push(SearchSourceForAddScreen())
            },
            onDeleteClick = viewModel::onDeleteFeeds,
        )
        ConsumeFlow(viewModel.finishScreenFlow) {
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
    private fun EditFeedsScreenContent(
        uiState: LoadableState<EditMixedContentUiState>,
        onRemoveSourceClick: (StatusSourceUiState) -> Unit,
        onEditNameClick: (String) -> Unit,
        onBackClick: () -> Unit,
        onAddSourceClick: () -> Unit,
        onDeleteClick: () -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        val errorMessage = uiState.successDataOrNull()?.errorMessage?.take(180)
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
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.surface,
                    onClick = onAddSourceClick,
                ) {
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
    uiState: LoadableState<EditMixedContentUiState>,
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
    val configName = uiState.successDataOrNull()?.name.orEmpty()
    Toolbar(
        title = configName,
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
    val loadedUiState = uiState.successDataOrNull()
    if (loadedUiState != null && showEditNameDialog) {
        var inputtedText by remember {
            mutableStateOf(configName)
        }
        FreadDialog(
            title = stringResource(Res.string.feeds_mixed_config_edit_new_name_dialog_title),
            onDismissRequest = { showEditNameDialog = false },
            onNegativeClick = { showEditNameDialog = false },
            onPositiveClick = {
                showEditNameDialog = false
                onEditNameClick(inputtedText)
            },
            content = {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    value = inputtedText,
                    onValueChange = {
                        inputtedText = it
                    },
                    label = {
                        Text(text = stringResource(Res.string.feeds_mixed_config_edit_new_name_dialog_label))
                    }
                )
            },
        )
    }
    if (showDeleteConfirmDialog) {
        AlertConfirmDialog(
            content = stringResource(Res.string.feeds_mixed_config_edit_delete_content_dialog_message),
            onDismissRequest = { showDeleteConfirmDialog = false },
            onConfirm = onDeleteClick,
        )
    }
}
