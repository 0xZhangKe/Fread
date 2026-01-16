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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.toast.toast
import com.zhangke.framework.utils.Locale
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.TextFieldUtils
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.PostStatusBottomBar
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.PostStatusPoll
import com.zhangke.fread.activitypub.app.internal.utils.DeleteTextUtil
import com.zhangke.fread.common.utils.MentionTextUtil
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMediaAttachment
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostScaffold
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.PostStatusVisibilityUi
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.PostStatusWarning
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.MultiAccountPublishingScreenKey
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.QuoteApprovalPolicy
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.ui.common.SelectAccountDialog
import com.zhangke.fread.status.ui.embed.UnavailableQuoteInEmbedding
import com.zhangke.fread.status.ui.embed.embedBorder
import com.zhangke.fread.status.ui.publish.BlogInQuoting
import com.zhangke.fread.status.ui.publish.PublishBlogStyle
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration

@Serializable
data class PostStatusScreenKey(
    val accountUri: FormalUri,
    val defaultContent: String? = null,
    val editBlogJsonString: String? = null,
    val replyingBlogJsonString: String? = null,
    val quoteBlogJsonString: String? = null,
) : NavKey

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PostStatusScreen(viewModel: PostStatusViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val loadableUiState by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    val snackMessageState = rememberSnackbarHostState()

    fun onBack() {
        if (loadableUiState !is LoadableState.Success) {
            backStack.removeLastOrNull()
            return
        }
        if (loadableUiState.requireSuccessData().hasInputtedData()) {
            showExitDialog = true
            return
        }
        backStack.removeLastOrNull()
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
            onQuoteApprovalPolicySelect = viewModel::onQuoteApprovalPolicySelect,
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
            onAddAccountClick = {
                val multiAccPublishScreen =
                    MultiAccountPublishingScreenKey.create(listOf(uiState.account))
                if (uiState.hasInputtedData()) {
                    backStack.add(multiAccPublishScreen)
                } else {
                    backStack.removeLastOrNull()
                    backStack.add(multiAccPublishScreen)
                }
            },
        )
    }
    val successMessage = stringResource(LocalizedString.postStatusSuccess)
    ConsumeFlow(viewModel.publishSuccessFlow) {
        toast(successMessage)
        backStack.removeLastOrNull()
    }
    BackHandler(true) {
        onBack()
    }
    if (showExitDialog) {
        FreadDialog(
            onDismissRequest = { showExitDialog = false },
            content = {
                Text(text = stringResource(LocalizedString.postStatusExitDialogContent))
            },
            onNegativeClick = {
                showExitDialog = false
            },
            onPositiveClick = {
                showExitDialog = false
                backStack.removeLastOrNull()
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
    onQuoteApprovalPolicySelect: (QuoteApprovalPolicy) -> Unit,
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
    onAddAccountClick: () -> Unit,
) {
    var showAccountSwitchPopup by remember { mutableStateOf(false) }
    PublishPostScaffold(
        account = uiState.account,
        snackBarHostState = snackMessageState,
        content = uiState.content,
        showSwitchAccountIcon = uiState.accountChangeable && uiState.availableAccountList.size > 1,
        showAddAccountIcon = uiState.showAddAccountIcon,
        publishing = uiState.publishing,
        replyingBlog = uiState.replyToBlog,
        onContentChanged = onContentChanged,
        onPublishClick = onPostClick,
        onBackClick = onCloseClick,
        onSwitchAccountClick = { showAccountSwitchPopup = true },
        onAddAccountClick = onAddAccountClick,
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
            if (uiState.rules.supportsQuotePost) {
                PublishInteractionSettingLabel(
                    modifier = Modifier,
                    visibility = uiState.visibility,
                    quoteApprovalPolicy = uiState.quoteApprovalPolicy,
                    visibilityChangeable = uiState.visibilityChangeable,
                    quoteApprovalPolicyChangeable = uiState.quoteApprovalPolicyChangeable,
                    onVisibilitySelect = onVisibilityChanged,
                    onQuoteApprovalPolicySelect = onQuoteApprovalPolicySelect,
                )
            } else {
                PostStatusVisibilityUi(
                    modifier = Modifier,
                    visibility = uiState.visibility,
                    changeable = uiState.visibilityChangeable,
                    onVisibilitySelect = onVisibilityChanged,
                )
            }
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
        attachment = { style ->
            StatusAttachment(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                uiState = uiState,
                style = style,
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
            selectedAccounts = listOf(uiState.account),
            onAccountClicked = { account ->
                onSwitchAccount(account)
            },
        )
    }
}

@Composable
private fun StatusAttachment(
    modifier: Modifier,
    uiState: PostStatusUiState,
    style: PublishBlogStyle,
    onDeleteClick: (PublishPostMedia) -> Unit,
    onDescriptionInputted: (PublishPostMedia, String) -> Unit,
    onPollContentChanged: (Int, String) -> Unit,
    onRemovePollClick: () -> Unit,
    onRemovePollItemClick: (Int) -> Unit,
    onAddPollItemClick: () -> Unit,
    onPollStyleSelect: (multiple: Boolean) -> Unit,
    onDurationSelect: (Duration) -> Unit,
) {
    if (uiState.quotingBlog != null) {
        BlogInQuoting(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            blog = uiState.quotingBlog,
            style = style.statusStyle,
        )
    } else if (uiState.unavailableQuote != null) {
        UnavailableQuoteInEmbedding(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
                .embedBorder()
                .padding(16.dp),
            unavailableQuote = uiState.unavailableQuote,
            onContentClick = {},
        )
    } else {
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
