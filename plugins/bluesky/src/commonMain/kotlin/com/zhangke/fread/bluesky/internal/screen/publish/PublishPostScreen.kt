package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.TwoTextsInRow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.bluesky.internal.model.ReplySetting
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.InputBlogTextField
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_blog_title
import com.zhangke.fread.status.model.IdentityRole
import org.jetbrains.compose.resources.stringResource

class PublishPostScreen(private val role: IdentityRole) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<PublishPostViewModel, PublishPostViewModel.Factory> {
            it.create(role)
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = rememberSnackbarHostState()
        PublishPostContent(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onBackClick = navigator::pop,
            onContentChanged = viewModel::onContentChanged,
            onQuoteChange = viewModel::onQuoteChange,
            onSettingSelected = viewModel::onReplySettingChange,
            onSettingOptionsSelected = viewModel::onSettingOptionsSelected,
            onMediaSelected = viewModel::onMediaSelected,
            onLanguageSelected = viewModel::onLanguageSelected,
            onMediaAltChanged = viewModel::onMediaAltChanged,
            onMediaDeleteClick = viewModel::onMediaDeleteClick,
            onPublishClick = viewModel::onPublishClick,
        )
    }

    @Composable
    private fun PublishPostContent(
        uiState: PublishPostUiState,
        snackBarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onContentChanged: (TextFieldValue) -> Unit,
        onQuoteChange: (Boolean) -> Unit,
        onSettingSelected: (ReplySetting) -> Unit,
        onSettingOptionsSelected: (ReplySetting.CombineOption) -> Unit,
        onMediaSelected: (List<PlatformUri>) -> Unit,
        onLanguageSelected: (List<String>) -> Unit,
        onMediaAltChanged: (PublishPostMediaAttachmentFile, String) -> Unit,
        onMediaDeleteClick: (PublishPostMediaAttachmentFile) -> Unit,
        onPublishClick: () -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.shared_publish_blog_title),
                    onBackClick = onBackClick,
                    actions = {
                        SimpleIconButton(
                            onClick = onPublishClick,
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Publish",
                        )
                    },
                )
            },
            snackbarHost = { SnackbarHost(snackBarHostState) },
            bottomBar = {
                PublishBottomPanel(
                    uiState = uiState,
                    onMediaSelected = onMediaSelected,
                    onLanguageSelected = onLanguageSelected,
                    selectedLanguages = uiState.selectedLanguages,
                    maxLanguageCount = uiState.maxLanguageCount,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    AutoSizeImage(
                        url = uiState.account?.avatar.orEmpty(),
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clip(CircleShape)
                            .size(42.dp),
                        contentDescription = null,
                    )

                    Column(
                        modifier = Modifier.weight(1F).padding(horizontal = 16.dp),
                    ) {
                        NameAndAccountInfo(
                            modifier = Modifier.fillMaxWidth(),
                            name = uiState.account?.userName.orEmpty(),
                            handle = uiState.account?.user?.prettyHandle.orEmpty(),
                        )
                        PostInteractionSettingLabel(
                            modifier = Modifier.padding(top = 2.dp),
                            setting = uiState.interactionSetting,
                            lists = uiState.list,
                            onQuoteChange = onQuoteChange,
                            onSettingSelected = onSettingSelected,
                            onSettingOptionsSelected = onSettingOptionsSelected,
                        )
                    }
                }
                InputBlogTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldValue = uiState.content,
                    onContentChanged = onContentChanged,
                )

                if (uiState.attachment != null) {
                    PublishPostMediaAttachment(
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                            .fillMaxWidth(),
                        media = uiState.attachment,
                        mediaAltMaxCharacters = uiState.mediaAltMaxCharacters,
                        onAltChanged = onMediaAltChanged,
                        onDeleteClick = onMediaDeleteClick,
                    )
                }
            }
        }
    }

    @Composable
    private fun NameAndAccountInfo(
        modifier: Modifier,
        name: String,
        handle: String,
    ) {
        TwoTextsInRow(
            firstText = {
                Text(
                    modifier = Modifier,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                )
            },
            secondText = {
                Text(
                    modifier = Modifier,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = handle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            spacing = 2.dp,
            modifier = modifier,
        )
    }
}
