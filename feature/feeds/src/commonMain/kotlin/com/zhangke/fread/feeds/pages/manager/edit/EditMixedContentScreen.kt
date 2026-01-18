package com.zhangke.fread.feeds.pages.manager.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.successDataOrNull
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.feeds.composable.RemovableStatusSource
import com.zhangke.fread.feeds.composable.StatusSourceUiState
import com.zhangke.fread.feeds.pages.manager.search.SearchSourceForAddScreenNavKey
import com.zhangke.fread.localization.LocalizedString
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class EditMixedContentScreenNavKey(val contentId: String) : NavKey

@Composable
fun EditMixedContentScreen(viewModel: EditMixedContentViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    EditFeedsScreenContent(
        uiState = uiState,
        onRemoveSourceClick = viewModel::onSourceDelete,
        onEditNameClick = viewModel::onEditName,
        onBackClick = backStack::removeLastOrNull,
        onAddSourceClick = {
            backStack.add(SearchSourceForAddScreenNavKey)
        },
        onDeleteClick = viewModel::onDeleteFeeds,
    )
    ConsumeFlow(SearchSourceForAddScreenNavKey.sourceSelectedFlow.flow) {
        viewModel.onAddSource(it)
    }
    ConsumeFlow(viewModel.finishScreenFlow) {
        backStack.removeLastOrNull()
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
                if (uiState.isSuccess) {
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.surface,
                        onClick = onAddSourceClick,
                        shape = CircleShape,
                    ) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Default.Add),
                            contentDescription = "Add Source",
                        )
                    }
                }
            },
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
                            onClick = {},
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
            title = stringResource(LocalizedString.feedsMixedConfigEditNewNameDialogTitle),
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
                        Text(text = stringResource(LocalizedString.feedsMixedConfigEditNewNameDialogLabel))
                    }
                )
            },
        )
    }
    if (showDeleteConfirmDialog) {
        AlertConfirmDialog(
            content = stringResource(LocalizedString.feedsMixedConfigEditDeleteContentDialogMessage),
            onDismissRequest = { showDeleteConfirmDialog = false },
            onConfirm = onDeleteClick,
        )
    }
}
