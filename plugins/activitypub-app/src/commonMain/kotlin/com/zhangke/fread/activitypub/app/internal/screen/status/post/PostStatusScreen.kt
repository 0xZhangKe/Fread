package com.zhangke.fread.activitypub.app.internal.screen.status.post

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.toast.toast
import com.zhangke.framework.utils.Locale
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.TextFieldUtils
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.PostStatusBottomBar
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.PostStatusPoll
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.PostStatusVisibilityUi
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.PostStatusWarning
import com.zhangke.fread.activitypub.app.internal.utils.DeleteTextUtil
import com.zhangke.fread.activitypub.app.post_status_exit_dialog_content
import com.zhangke.fread.activitypub.app.post_status_success
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.common.utils.MentionTextUtil
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMediaAttachment
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostScaffold
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.ui.common.SelectAccountDialog
import com.zhangke.fread.status.uri.FormalUri
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration

class PostStatusScreen(
    private val accountUri: FormalUri,
    private val editBlogJsonString: String? = null,
    private val replyingBlogJsonString: String? = null,
) : BaseScreen() {

    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<PostStatusViewModel, PostStatusViewModel.Factory> {
            it.create(
                PostStatusScreenRoute.buildParams(
                    accountUri = accountUri,
                    editBlog = editBlogJsonString,
                    replyToBlogJsonString = replyingBlogJsonString,
                )
            )
        }
        val loadableUiState by viewModel.uiState.collectAsState()
        var showExitDialog by remember {
            mutableStateOf(false)
        }

        val snackMessageState = rememberSnackbarHostState()

        fun onBack() {
            if (loadableUiState !is LoadableState.Success) {
                navigator.pop()
                return
            }
            if (loadableUiState.requireSuccessData().hasInputtedData()) {
                showExitDialog = true
                return
            }
            navigator.pop()
        }
        LoadableLayout(
            modifier = Modifier.fillMaxSize(),
            state = loadableUiState,
        ) { uiState ->
            PostStatusScreenContent(
                uiState = uiState,
                snackMessageState = snackMessageState,
                onSwitchAccount = viewModel::onSwitchAccountClick,
                onContentChanged = viewModel::onContentChanged,
                onCloseClick = { onBack() },
                onPostClick = viewModel::onPostClick,
                onSensitiveClick = viewModel::onSensitiveClick,
                onMediaSelected = viewModel::onMediaSelected,
                onLanguageSelected = viewModel::onLanguageSelected,
                onDeleteClick = viewModel::onMediaDeleteClick,
                onDescriptionInputted = viewModel::onDescriptionInputted,
                onPollClicked = viewModel::onPollClicked,
                onPollContentChanged = viewModel::onPollContentChanged,
                onAddPollItemClick = viewModel::onAddPollItemClick,
                onRemovePollClick = viewModel::onRemovePollClick,
                onRemovePollItemClick = viewModel::onRemovePollItemClick,
                onPollStyleSelect = viewModel::onPollStyleSelect,
                onWarningContentChanged = viewModel::onWarningContentChanged,
                onVisibilityChanged = viewModel::onVisibilityChanged,
                onDurationSelect = viewModel::onDurationSelect,
            )
        }
        val successMessage = stringResource(Res.string.post_status_success)
        ConsumeFlow(viewModel.publishSuccessFlow) {
            toast(successMessage)
            navigator.pop()
        }
        BackHandler(true) {
            onBack()
        }
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
        ConsumeSnackbarFlow(snackMessageState, viewModel.snackMessage)
    }

    @Composable
    private fun PostStatusScreenContent(
        uiState: PostStatusUiState,
        snackMessageState: SnackbarHostState,
        onSwitchAccount: (LoggedAccount) -> Unit,
        onContentChanged: (TextFieldValue) -> Unit,
        onCloseClick: () -> Unit,
        onPostClick: () -> Unit,
        onSensitiveClick: () -> Unit,
        onMediaSelected: (List<PlatformUri>) -> Unit,
        onDeleteClick: (PublishPostMedia) -> Unit,
        onDescriptionInputted: (PublishPostMedia, String) -> Unit,
        onLanguageSelected: (Locale) -> Unit,
        onPollClicked: () -> Unit,
        onPollContentChanged: (Int, String) -> Unit,
        onRemovePollClick: () -> Unit,
        onRemovePollItemClick: (Int) -> Unit,
        onAddPollItemClick: () -> Unit,
        onPollStyleSelect: (multiple: Boolean) -> Unit,
        onWarningContentChanged: (TextFieldValue) -> Unit,
        onVisibilityChanged: (StatusVisibility) -> Unit,
        onDurationSelect: (Duration) -> Unit,
    ) {
        var showAccountSwitchPopup by remember { mutableStateOf(false) }
        PublishPostScaffold(
            account = uiState.account,
            snackBarHostState = snackMessageState,
            content = uiState.content,
            showSwitchAccountIcon = uiState.accountChangeable && uiState.availableAccountList.size > 1,
            showAddAccountIcon = uiState.accountChangeable && uiState.availableAccountList.size > 1,
            publishing = uiState.publishing,
            replyingBlog = uiState.replyToBlog,
            onContentChanged = onContentChanged,
            onPublishClick = onPostClick,
            onBackClick = onCloseClick,
            onSwitchAccountClick = { showAccountSwitchPopup = true },
            onAddAccountClick = {},
            contentWarning = {
                if (uiState.sensitive) {
                    PostStatusWarning(
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        warning = uiState.warningContent,
                        onValueChanged = onWarningContentChanged,
                    )
                }
            },
            postSettingLabel = {
                PostStatusVisibilityUi(
                    modifier = Modifier,
                    visibility = uiState.visibility,
                    changeable = uiState.visibilityChangeable,
                    onVisibilitySelect = onVisibilityChanged,
                )
            },
            bottomPanel = {
                PostStatusBottomBar(
                    uiState = uiState,
                    onSensitiveClick = onSensitiveClick,
                    onMediaSelected = onMediaSelected,
                    onLanguageSelected = onLanguageSelected,
                    onPollClicked = onPollClicked,
                    onEmojiPick = {
                        onContentChanged(
                            TextFieldUtils.insertText(
                                value = uiState.content,
                                insertText = " :${it.shortcode}: ",
                            )
                        )
                    },
                    onMentionClick = {
                        onContentChanged(
                            MentionTextUtil.insertMention(
                                text = uiState.content,
                                insertText = it.acct,
                            )
                        )
                    },
                    onDeleteEmojiClick = {
                        onContentChanged(DeleteTextUtil.deleteText(uiState.content))
                    },
                )
            },
            attachment = {
                StatusAttachment(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    uiState = uiState,
                    onDeleteClick = onDeleteClick,
                    onDescriptionInputted = onDescriptionInputted,
                    onPollContentChanged = onPollContentChanged,
                    onRemovePollClick = onRemovePollClick,
                    onRemovePollItemClick = onRemovePollItemClick,
                    onAddPollItemClick = onAddPollItemClick,
                    onPollStyleSelect = onPollStyleSelect,
                    onDurationSelect = onDurationSelect,
                )
            },
        )

        if (showAccountSwitchPopup) {
            SelectAccountDialog(
                accountList = uiState.availableAccountList,
                onDismissRequest = { showAccountSwitchPopup = false },
                selectedAccount = uiState.account,
                onSwitchAccount = { account ->
                    onSwitchAccount(account)
                },
            )
        }
    }

    @Composable
    private fun StatusAttachment(
        modifier: Modifier,
        uiState: PostStatusUiState,
        onDeleteClick: (PublishPostMedia) -> Unit,
        onDescriptionInputted: (PublishPostMedia, String) -> Unit,
        onPollContentChanged: (Int, String) -> Unit,
        onRemovePollClick: () -> Unit,
        onRemovePollItemClick: (Int) -> Unit,
        onAddPollItemClick: () -> Unit,
        onPollStyleSelect: (multiple: Boolean) -> Unit,
        onDurationSelect: (Duration) -> Unit,
    ) {
        val attachment = uiState.attachment ?: return
        when (attachment) {
            is PostStatusAttachment.Image -> {
                PublishPostMediaAttachment(
                    modifier = modifier
                        .padding(horizontal = 16.dp),
                    medias = attachment.imageList,
                    mediaAltMaxCharacters = uiState.rules.altMaxCharacters,
                    onAltChanged = onDescriptionInputted,
                    onDeleteClick = onDeleteClick,
                )
            }

            is PostStatusAttachment.Video -> {
                PublishPostMediaAttachment(
                    modifier = modifier
                        .padding(horizontal = 16.dp),
                    medias = listOf(attachment.video),
                    mediaAltMaxCharacters = uiState.rules.altMaxCharacters,
                    onAltChanged = onDescriptionInputted,
                    onDeleteClick = onDeleteClick,
                )
            }

            is PostStatusAttachment.Poll -> {
                PostStatusPoll(
                    modifier = modifier,
                    poll = attachment,
                    rules = uiState.rules,
                    onPollContentChanged = onPollContentChanged,
                    onRemovePollClick = onRemovePollClick,
                    onRemoveItemClick = onRemovePollItemClick,
                    onAddPollItemClick = onAddPollItemClick,
                    onPollStyleSelect = onPollStyleSelect,
                    onDurationSelect = onDurationSelect,
                )
            }
        }
    }
}
