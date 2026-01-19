package com.zhangke.fread.feeds.pages.manager.importing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.zhangke.framework.composable.BackHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.utils.LocalPlatformUriHelper
import com.zhangke.fread.common.utils.LocalToastHelper
import com.zhangke.fread.localization.LocalizedString
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Serializable
object ImportFeedsScreenNavKey : NavKey

@Composable
fun ImportFeedsScreen(viewModel: ImportFeedsViewModel) {
    val toastHelper = LocalToastHelper.current
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    var showBackDialog by remember {
        mutableStateOf(false)
    }

    fun onBackRequest() {
        if (uiState.selectedFileUri != null || uiState.sourceList.isNotEmpty()) {
            showBackDialog = true
        } else {
            backStack.removeLastOrNull()
        }
    }
    if (showBackDialog) {
        FreadDialog(
            onDismissRequest = {
                showBackDialog = false
            },
            title = stringResource(LocalizedString.alert),
            contentText = stringResource(LocalizedString.feedsImportBackDialogMessage),
            onNegativeClick = {
                showBackDialog = false
            },
            onPositiveClick = {
                backStack.removeLastOrNull()
            }
        )
    }
    BackHandler(true) {
        onBackRequest()
    }
    ImportFeedsContent(
        uiState = uiState,
        onBackClick = ::onBackRequest,
        onFileSelected = viewModel::onFileSelected,
        onImportClick = {
            viewModel.onImportClick()
        },
        onGroupDelete = viewModel::onGroupDelete,
        onSourceDelete = viewModel::onSourceDelete,
        onSaveClick = viewModel::onSaveClick,
        retryImportClick = viewModel::retryImportClick,
    )
    ConsumeFlow(viewModel.saveSuccessFlow) {
        toastHelper.showToast(getString(LocalizedString.addContentSuccessSnackbar))
        backStack.removeLastOrNull()
    }
}

@Composable
private fun ImportFeedsContent(
    uiState: ImportFeedsUiState,
    onFileSelected: (PlatformUri) -> Unit,
    onBackClick: () -> Unit,
    onImportClick: () -> Unit,
    onGroupDelete: (ImportSourceGroup) -> Unit,
    onSourceDelete: (ImportSourceGroup, ImportingSource) -> Unit,
    retryImportClick: (ImportSourceGroup, ImportingSource) -> Unit,
    onSaveClick: () -> Unit,
) {
    val platformUriHelper = LocalPlatformUriHelper.current
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.feedsImportPageTitle),
                onBackClick = onBackClick,
                actions = {
                    SimpleIconButton(
                        onClick = onSaveClick,
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                    )
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OpenDocumentContainer(
                    onResult = onFileSelected,
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                launch()
                            },
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            val prettyFileUri = remember(uiState.selectedFileUri) {
                                uiState.selectedFileUri?.let {
                                    platformUriHelper.queryFileName(it)
                                }
                            }
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = prettyFileUri
                                    ?: stringResource(LocalizedString.feedsImportPageHint),
                                overflow = TextOverflow.Clip,
                                maxLines = 1,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
                Button(
                    modifier = Modifier.padding(start = 16.dp),
                    onClick = onImportClick,
                    enabled = uiState.selectedFileUri != null,
                ) {
                    Text(
                        text = stringResource(LocalizedString.feedsImportButton)
                    )
                }
            }

            if (uiState.errorMessage.isNullOrEmpty().not()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    text = uiState.errorMessage.orEmpty(),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            ImportGroupList(
                itemList = uiState.importingUiItems,
                onGroupDelete = onGroupDelete,
                onSourceDelete = onSourceDelete,
                retryImportClick = retryImportClick,
            )
        }
    }
}

@Composable
private fun ImportGroupList(
    itemList: List<ImportingUiItem>,
    onGroupDelete: (ImportSourceGroup) -> Unit,
    onSourceDelete: (ImportSourceGroup, ImportingSource) -> Unit,
    retryImportClick: (ImportSourceGroup, ImportingSource) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
    ) {
        items(itemList) { item ->
            when (item) {
                is ImportingUiItem.Group -> {
                    ImportGroupItem(
                        group = item.group,
                        onGroupDelete = onGroupDelete,
                    )
                }

                is ImportingUiItem.Source -> {
                    ImportSourceItem(
                        group = item.group,
                        source = item.source,
                        onSourceDelete = onSourceDelete,
                        retryImportClick = retryImportClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportGroupItem(
    group: ImportSourceGroup,
    onGroupDelete: (ImportSourceGroup) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier,
            text = group.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.weight(1F))
        var showDeleteDialog by remember {
            mutableStateOf(false)
        }
        Text(
            text = "${group.children.size} sources",
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.width(8.dp))
        SimpleIconButton(
            onClick = { showDeleteDialog = true },
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete group",
        )
        if (showDeleteDialog) {
            FreadDialog(
                onDismissRequest = { showDeleteDialog = false },
                contentText = stringResource(LocalizedString.feedsDeleteConfirmContent),
                onNegativeClick = { showDeleteDialog = false },
                onPositiveClick = {
                    showDeleteDialog = false
                    onGroupDelete(group)
                },
            )
        }
    }
}

@Composable
private fun ImportSourceItem(
    group: ImportSourceGroup,
    source: ImportingSource,
    onSourceDelete: (ImportSourceGroup, ImportingSource) -> Unit,
    retryImportClick: (ImportSourceGroup, ImportingSource) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1F),
        ) {
            Text(
                modifier = Modifier,
                text = source.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (source is ImportingSource.Failure) {
                Text(
                    text = source.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        when (source) {
            is ImportingSource.Importing -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp)
                )
            }

            is ImportingSource.Success -> {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                )
            }

            is ImportingSource.Pending -> {
                Text(text = "waiting...")
            }

            is ImportingSource.Failure -> {
                Icon(
                    modifier = Modifier.clickable { retryImportClick(group, source) },
                    painter = rememberVectorPainter(image = Icons.Default.Refresh),
                    contentDescription = "Retry",
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        var showDeleteDialog by remember {
            mutableStateOf(false)
        }
        SimpleIconButton(
            onClick = { showDeleteDialog = true },
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete source",
        )
        if (showDeleteDialog) {
            FreadDialog(
                onDismissRequest = { showDeleteDialog = false },
                contentText = stringResource(LocalizedString.feedsDeleteConfirmContent),
                onNegativeClick = { showDeleteDialog = false },
                onPositiveClick = {
                    showDeleteDialog = false
                    onSourceDelete(group, source)
                },
            )
        }
    }
}
