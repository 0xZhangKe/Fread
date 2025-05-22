package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.toast.toast
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.languageCode
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.post_status_exit_dialog_content
import com.zhangke.fread.commonbiz.shared.screen.post_status_success
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostFeaturesPanel
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMediaAttachment
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishTopBar
import com.zhangke.fread.commonbiz.shared.screen.publish.SensitiveIconButton
import com.zhangke.fread.commonbiz.shared.screen.publish.bottomPaddingAsBottomBar
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.InputBlogTextField
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.PostInteractionSettingLabel
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.PostStatusVisibilityUi
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.PostStatusWarning
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_blog_text_hint
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PostInteractionSetting
import com.zhangke.fread.status.model.StatusVisibility
import org.jetbrains.compose.resources.stringResource

class MultiAccountPublishingScreen(
    private val userUrisJson: String,
) : BaseScreen() {

    companion object {

        fun createInstance(accounts: List<LoggedAccount>): Screen {
            return MultiAccountPublishingScreen(
                userUrisJson = globalJson.encodeToString(accounts.map { it.uri.toString() }),
            )
        }
    }

    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val snackBarHostState = rememberSnackbarHostState()
        val viewModel =
            getViewModel<MultiAccountPublishingViewModel, MultiAccountPublishingViewModel.Factory> {
                it.create(globalJson.decodeFromString(userUrisJson))
            }
        val uiState by viewModel.uiState.collectAsState()
        var showExitDialog by remember { mutableStateOf(false) }
        fun onBack() {
            if (uiState.hasInputtedData) {
                showExitDialog = true
                return
            }
            navigator.pop()
        }
        MultiAccountPublishingContent(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onBackClick = navigator::pop,
            onPublishClick = viewModel::onPublishClick,
            onMediaSelected = viewModel::onMediaSelected,
            onLanguageSelected = viewModel::onLanguageSelected,
            onRemoveAccountClick = viewModel::onRemoveAccountClick,
            onContentChanged = viewModel::onContentChanged,
            onSensitiveClick = viewModel::onSensitiveClick,
            onWarningContentChanged = viewModel::onWarningContentChanged,
            onMediaAltChanged = viewModel::onMediaAltChanged,
            onDeleteMediaClick = viewModel::onDeleteMediaClick,
            onVisibilitySelect = viewModel::onVisibilitySelect,
            onSettingSelected = viewModel::onSettingSelected,
            onAddAccountClick = viewModel::onAddAccount,
        )
        val successMessage = stringResource(Res.string.post_status_success)
        ConsumeFlow(viewModel.publishSuccessFlow) {
            toast(successMessage)
            navigator.pop()
        }
        BackHandler(true) { onBack() }
        if (showExitDialog) {
            FreadDialog(
                onDismissRequest = { showExitDialog = false },
                content = {
                    Text(text = stringResource(Res.string.post_status_exit_dialog_content))
                },
                onNegativeClick = {
                    showExitDialog = false
                },
                onPositiveClick = {
                    showExitDialog = false
                    navigator.pop()
                },
            )
        }
        ConsumeSnackbarFlow(snackBarHostState, viewModel.snackMessage)
    }

    @Composable
    private fun MultiAccountPublishingContent(
        uiState: MultiAccountPublishingUiState,
        snackBarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onPublishClick: () -> Unit,
        onMediaSelected: (List<PlatformUri>) -> Unit,
        onLanguageSelected: (String) -> Unit,
        onRemoveAccountClick: (LoggedAccount) -> Unit,
        onContentChanged: (TextFieldValue) -> Unit,
        onSensitiveClick: () -> Unit,
        onWarningContentChanged: (TextFieldValue) -> Unit,
        onMediaAltChanged: (PublishPostMedia, String) -> Unit,
        onDeleteMediaClick: (PublishPostMedia) -> Unit,
        onVisibilitySelect: (StatusVisibility) -> Unit,
        onSettingSelected: (PostInteractionSetting) -> Unit,
        onAddAccountClick: (MultiPublishingAccountWithRules) -> Unit,
    ) {
        Scaffold(
            topBar = {
                PublishTopBar(
                    publishing = uiState.publishing,
                    onBackClick = onBackClick,
                    onPublishClick = onPublishClick,
                )
            },
            snackbarHost = { SnackbarHost(snackBarHostState) },
            bottomBar = {
                PublishPostFeaturesPanel(
                    modifier = Modifier.fillMaxWidth().bottomPaddingAsBottomBar(),
                    contentLength = uiState.content.text.length,
                    maxContentLimit = uiState.globalRules.maxCharacters,
                    mediaAvailableCount = uiState.mediaAvailableCount,
                    onMediaSelected = onMediaSelected,
                    containerColor = MaterialTheme.colorScheme.background,
                    selectedLanguages = listOf(uiState.selectedLanguage.languageCode),
                    maxLanguageCount = uiState.globalRules.maxLanguageCount,
                    onLanguageSelected = {
                        it.firstOrNull()?.let { lan -> onLanguageSelected(lan) }
                    },
                    actions = {
                        SensitiveIconButton(onSensitiveClick = onSensitiveClick)
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                PublishingAccounts(
                    modifier = Modifier.fillMaxWidth(),
                    uiState = uiState,
                    onRemoveAccountClick = onRemoveAccountClick,
                    onAddAccountClick = onAddAccountClick,
                )
                if (uiState.showInteractionSetting || uiState.showPostVisibilitySetting) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (uiState.showPostVisibilitySetting) {
                            PostStatusVisibilityUi(
                                modifier = Modifier,
                                changeable = true,
                                visibility = uiState.postVisibility,
                                onVisibilitySelect = onVisibilitySelect,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        if (uiState.showInteractionSetting) {
                            PostInteractionSettingLabel(
                                modifier = Modifier,
                                setting = uiState.interactionSetting,
                                lists = emptyList(),
                                onSettingSelected = onSettingSelected,
                            )
                        }
                    }
                }
                if (uiState.sensitive) {
                    PostStatusWarning(
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        warning = uiState.warningContent,
                        onValueChanged = onWarningContentChanged,
                    )
                }
                InputBlogTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textFieldValue = uiState.content,
                    onContentChanged = onContentChanged,
                    mentionHighlightEnabled = false,
                    placeholder = buildAnnotatedString {
                        append(stringResource(Res.string.shared_publish_blog_text_hint))
                    },
                )
                PublishPostMediaAttachment(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    medias = uiState.medias,
                    mediaAltMaxCharacters = uiState.globalRules.mediaAltMaxCharacters,
                    onAltChanged = onMediaAltChanged,
                    onDeleteClick = onDeleteMediaClick,
                )
            }
        }
    }
}
