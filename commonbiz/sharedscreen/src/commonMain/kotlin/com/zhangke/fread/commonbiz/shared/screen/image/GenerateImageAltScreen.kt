package com.zhangke.fread.commonbiz.shared.screen.image

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.common.ai.image.ImageAiModelDownloadState
import com.zhangke.fread.common.ai.image.ImageDescriptionGenerateState
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource

class GenerateImageAltScreen(private val imageUri: String) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<GenerateImageAltViewModel, GenerateImageAltViewModel.Factory> {
            it.create(imageUri)
        }
        val uiState by viewModel.uiState.collectAsState()
        GenerateImageAltScreenContent(
            uiState = uiState,
            onBackClick = { navigator.pop() },
            onGenerateClick = viewModel::onGenerateClick,
            onGenerateFailedClick = viewModel::onGenerateFailedClick,
            onDownloadClick = viewModel::onDownloadClick,
            onDownloadCancel = viewModel::onDownloadCancelClick,
            onDoNotDownloadClick = viewModel::onDoNotDownloadClick,
            onDownloadFailureClick = viewModel::onDownloadFailureClick,
            onDownloadSuccessClick = viewModel::onDownloadSuccessClick,
        )
    }

    @Composable
    private fun GenerateImageAltScreenContent(
        uiState: GenerateImageAltUiState,
        onBackClick: () -> Unit,
        onGenerateClick: () -> Unit,
        onDoNotDownloadClick: () -> Unit,
        onGenerateFailedClick: () -> Unit,
        onDownloadCancel: () -> Unit,
        onDownloadClick: () -> Unit,
        onDownloadFailureClick: () -> Unit,
        onDownloadSuccessClick: () -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(LocalizedString.post_status_image_generate_alt),
                    onBackClick = onBackClick,
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                Card(
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 16.dp)
                        .fillMaxWidth()
                        .aspectRatio(1.7F),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        AutoSizeImage(
                            url = uiState.imageUri,
                            modifier = Modifier.align(Alignment.Center),
                            contentDescription = null,
                        )
                    }
                }

                Text(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    text = stringResource(LocalizedString.post_status_image_generate_alt_hint),
                    style = MaterialTheme.typography.labelMedium,
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.medium,
                        )
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp),
                        text = uiState.generatedText,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    onClick = onGenerateClick,
                    enabled = uiState.generatingState !is ImageDescriptionGenerateState.Generating,
                ) {
                    if (uiState.generatingState is ImageDescriptionGenerateState.Generating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = stringResource(LocalizedString.post_status_image_generate_alt_generate)
                    )
                }
                if (uiState.generatingState is ImageDescriptionGenerateState.Downloadable) {
                    FreadDialog(
                        onDismissRequest = {},
                        properties = DialogProperties(
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false,
                        ),
                        contentText = stringResource(LocalizedString.post_status_image_generate_alt_downloadable_content),
                        onNegativeClick = onDoNotDownloadClick,
                        onPositiveClick = onDownloadClick,
                    )
                }
                if (uiState.generatingState is ImageDescriptionGenerateState.Unavailable) {
                    FreadDialog(
                        onDismissRequest = {},
                        properties = DialogProperties(
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false,
                        ),
                        contentText = stringResource(LocalizedString.post_status_image_generate_alt_generator_unavailable),
                        onPositiveClick = onGenerateFailedClick,
                    )
                }
                if (uiState.generatingState is ImageDescriptionGenerateState.Failure) {
                    FreadDialog(
                        onDismissRequest = {},
                        properties = DialogProperties(
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false,
                        ),
                        contentText = stringResource(
                            LocalizedString.post_status_image_generate_alt_generating_failed,
                            uiState.generatingState.error.message.orEmpty(),
                        ),
                        onPositiveClick = onGenerateFailedClick,
                    )
                }
                if (uiState.downloadState is ImageAiModelDownloadState.Success) {
                    FreadDialog(
                        onDismissRequest = {},
                        properties = DialogProperties(
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false,
                        ),
                        contentText = stringResource(LocalizedString.post_status_image_generate_alt_downloading_success),
                        onPositiveClick = onDownloadSuccessClick,
                    )
                }
                if (uiState.downloadState is ImageAiModelDownloadState.Failure) {
                    FreadDialog(
                        onDismissRequest = {},
                        properties = DialogProperties(
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false,
                        ),
                        contentText = stringResource(LocalizedString.post_status_image_generate_alt_downloading_failure),
                        onPositiveClick = onDownloadFailureClick,
                    )
                }
                if (uiState.downloadState is ImageAiModelDownloadState.Started ||
                    uiState.downloadState is ImageAiModelDownloadState.Downloading
                ) {
                    FreadDialog(
                        onDismissRequest = {},
                        properties = DialogProperties(
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false,
                        ),
                        title = stringResource(LocalizedString.post_status_image_generate_alt_downloading),
                        content = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp)
                            )
                        },
                        onNegativeClick = onDownloadCancel,
                    )
                }
            }
        }
    }
}
