package com.zhangke.fread.feeds.pages.manager.importing

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.media.MediaFileUtil
import com.zhangke.fread.feeds.R
import com.zhangke.fread.feeds.pages.manager.add.showAddContentSuccessToast
import com.zhangke.fread.status.model.ContentConfig
import kotlinx.coroutines.delay

class ImportFeedsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<ImportFeedsViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current
        var showBackDialog by remember {
            mutableStateOf(false)
        }

        fun onBackRequest() {
            if (uiState.selectedFileUri != null || uiState.parsedContent.isNotEmpty()) {
                showBackDialog = true
            } else {
                navigator.pop()
            }
        }
        if (showBackDialog) {
            FreadDialog(
                onDismissRequest = {
                    showBackDialog = false
                },
                title = stringResource(com.zhangke.fread.framework.R.string.alert),
                contentText = stringResource(R.string.feeds_import_back_dialog_message),
                onNegativeClick = {
                    showBackDialog = false
                },
                onPositiveClick = {
                    navigator.pop()
                }
            )
        }
        BackHandler {
            onBackRequest()
        }
        ImportFeedsContent(
            uiState = uiState,
            onBackClick = ::onBackRequest,
            onFileSelected = viewModel::onFileSelected,
            onImportClick = {
                viewModel.onImportClick(context)
            },
            onImportCancelClick = viewModel::onImportCancelClick,
            onImportDialogConfirmClick = viewModel::onImportDialogConfirmClick,
            onContentConfigDelete = viewModel::onContentConfigDelete,
            onSaveClick = viewModel::onSaveClick,
        )
        ConsumeFlow(viewModel.saveSuccessFlow) {
            showAddContentSuccessToast(context)
            navigator.pop()
        }
    }

    @Composable
    private fun ImportFeedsContent(
        uiState: ImportFeedsUiState,
        onFileSelected: (Uri) -> Unit,
        onBackClick: () -> Unit,
        onImportClick: () -> Unit,
        onImportCancelClick: () -> Unit,
        onImportDialogConfirmClick: () -> Unit,
        onContentConfigDelete: (ContentConfig) -> Unit,
        onSaveClick: () -> Unit,
    ) {
        val context = LocalContext.current
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(R.string.feeds_import_page_title),
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
                    val selectedFileLauncher =
                        rememberLauncherForActivityResult(OpenDocument()) { uri ->
                            if (uri != null) {
                                onFileSelected(uri)
                            }
                        }
                    Card(
                        modifier = Modifier
                            .weight(1F)
                            .clickable {
                                selectedFileLauncher.launch(arrayOf("*/*"))
                            },
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            val prettyFileUri = remember(uiState.selectedFileUri) {
                                if (uiState.selectedFileUri == null) {
                                    null
                                } else {
                                    MediaFileUtil.queryFileName(context, uiState.selectedFileUri)
                                }
                            }
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = prettyFileUri ?: stringResource(R.string.feeds_import_page_hint),
                                overflow = TextOverflow.Clip,
                                maxLines = 1,
                                fontSize = 12.sp,
                            )
                        }
                    }
                    Button(
                        modifier = Modifier.padding(start = 16.dp),
                        onClick = onImportClick,
                        enabled = uiState.selectedFileUri != null,
                    ) {
                        Text(
                            text = stringResource(R.string.feeds_import_button)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                        .padding(top = 16.dp, bottom = 16.dp),
                ) {
                    items(uiState.parsedContent) { contentConfig ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier
                                        .alignByBaseline(),
                                    text = contentConfig.configName,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                val desc = "${(contentConfig as ContentConfig.MixedContent).sourceUriList.size} items"
                                Text(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .alignByBaseline(),
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Spacer(modifier = Modifier.weight(1F))
                                SimpleIconButton(
                                    onClick = { onContentConfigDelete(contentConfig) },
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete content",
                                )
                            }
                        }
                    }
                }
            }
            ImportingState(
                uiState = uiState,
                onCancelClick = onImportCancelClick,
            )
        }
    }

    @Composable
    private fun ImportingState(
        uiState: ImportFeedsUiState,
        onCancelClick: () -> Unit,
    ) {
        if (uiState.importType != ImportType.IDLE) {
            val importType = uiState.importType
            val title = when (importType) {
                ImportType.IMPORTING -> "Importing..."
                ImportType.SUCCESS -> "Import Success"
                ImportType.FAILED -> "Failure!"
                else -> ""
            }
            FreadDialog(
                title = title,
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                ),
                onDismissRequest = {},
                onNegativeClick = {
                    if (importType == ImportType.IMPORTING) {
                        onCancelClick()
                    }
                },
                onPositiveClick = {
                    if (importType == ImportType.SUCCESS || importType == ImportType.FAILED) {
                        onCancelClick()
                    }
                },
                content = {
                    val lazyListState = rememberLazyListState()
                    val configuration = LocalConfiguration.current
                    val screenHeight = configuration.screenHeightDp.dp * 0.7F
                    LaunchedEffect(uiState.outputInfoList) {
                        // delay for LazyColumn items layout
                        delay(50)
                        lazyListState.animateScrollToItem(uiState.outputInfoList.lastIndex)
                    }
                    LazyColumn(
                        modifier = Modifier.heightIn(max = screenHeight),
                        state = lazyListState,
                    ) {
                        items(uiState.outputInfoList) { log ->
                            OutputLog(log = log)
                        }
                    }
                }
            )
        }
    }

    @Composable
    private fun OutputLog(log: ImportOutputLog) {
        val fontColor = when (log.type) {
            ImportOutputLog.Type.NORMAL -> MaterialTheme.colorScheme.onSurface
            ImportOutputLog.Type.WARNING -> Color.Yellow.copy(alpha = 0.7F)
            ImportOutputLog.Type.ERROR -> MaterialTheme.colorScheme.error
        }
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 2.dp, end = 16.dp, bottom = 2.dp),
            text = log.log,
            color = fontColor,
            fontSize = 10.sp,
        )
    }
}
