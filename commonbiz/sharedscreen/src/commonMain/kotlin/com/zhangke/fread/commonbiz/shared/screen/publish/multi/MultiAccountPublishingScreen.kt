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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.languageCode
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.PostInteractionSetting
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostFeaturesPanel
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMediaAttachment
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishTopBar
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.AvatarsHorizontalStack
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.InputBlogTextField
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.PostInteractionSettingLabel
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.PostStatusVisibilityUi
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.PostStatusWarning
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_blog_text_hint
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.statusui.ic_post_status_spoiler
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class MultiAccountPublishingScreen(
    private val userUrisJson: String,
) : BaseScreen() {

    companion object {

        fun open(navigator: Navigator, accounts: List<LoggedAccount>) {
            navigator.push(
                MultiAccountPublishingScreen(
                    userUrisJson = globalJson.encodeToString(accounts.map { it.uri.toString() }),
                )
            )
        }
    }

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
                    modifier = Modifier.fillMaxWidth(),
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
                        SimpleIconButton(
                            modifier = Modifier.padding(start = 4.dp),
                            onClick = onSensitiveClick,
                            painter = painterResource(com.zhangke.fread.statusui.Res.drawable.ic_post_status_spoiler),
                            contentDescription = "Sensitive content",
                        )
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
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                ) {
                    AvatarsHorizontalStack(
                        modifier = Modifier.padding(top = 8.dp),
                        avatars = uiState.addedAccounts.map { it.account.avatar },
                    )
                    Column(modifier = Modifier.weight(1F)) {
                        if (uiState.showInteractionSetting || uiState.showPostVisibilitySetting) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
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
                    mediaAltMaxCharacters = uiState.globalRules.maxMediaCount,
                    onAltChanged = onMediaAltChanged,
                    onDeleteClick = onDeleteMediaClick,
                )
            }
        }
    }
}
