package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import app.bsky.actor.ProfileView
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostFeaturesPanel
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostScaffold
import com.zhangke.fread.commonbiz.shared.screen.publish.bottomPaddingAsBottomBar
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.PostInteractionSettingLabel
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.MultiAccountPublishingScreenKey
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.ReplySetting
import com.zhangke.fread.status.ui.publish.BlogInQuoting
import kotlinx.serialization.Serializable

@Serializable
data class PublishPostScreenNavKey(
    val locator: PlatformLocator,
    val defaultText: String? = null,
    val replyToJsonString: String? = null,
    val quoteJsonString: String? = null,
) : NavKey

@Composable
fun PublishPostScreen(viewModel: PublishPostViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = rememberSnackbarHostState()
    PublishPostContent(
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onBackClick = backStack::removeLastOrNull,
        onContentChanged = viewModel::onContentChanged,
        onQuoteChange = viewModel::onQuoteChange,
        onSettingSelected = viewModel::onReplySettingChange,
        onSettingOptionsSelected = viewModel::onSettingOptionsSelected,
        onMediaSelected = viewModel::onMediaSelected,
        onLanguageSelected = viewModel::onLanguageSelected,
        onMediaAltChanged = viewModel::onMediaAltChanged,
        onMediaDeleteClick = viewModel::onMediaDeleteClick,
        onPublishClick = viewModel::onPublishClick,
        onAddAccountClick = {
            val key = MultiAccountPublishingScreenKey.create(
                uiState.account?.let { listOf(it) }.orEmpty(),
            )
            if (uiState.hasInputtedData) {
                backStack.add(key)
            } else {
                backStack.removeLastOrNull()
                backStack.add(key)
            }
        },
        onMentionCandidateClick = viewModel::onMentionCandidateClick,
    )
    ConsumeSnackbarFlow(snackBarHostState, viewModel.snackBarMessageFlow)
    ConsumeFlow(viewModel.finishPageFlow) {
        backStack.removeLastOrNull()
    }
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
    onMediaAltChanged: (PublishPostMedia, String) -> Unit,
    onMediaDeleteClick: (PublishPostMedia) -> Unit,
    onPublishClick: () -> Unit,
    onAddAccountClick: () -> Unit,
    onMentionCandidateClick: (ProfileView) -> Unit,
) {
    PublishPostScaffold(
        account = uiState.account,
        snackBarHostState = snackBarHostState,
        content = uiState.content,
        showSwitchAccountIcon = false,
        showAddAccountIcon = uiState.showAddAccountIcon,
        publishing = uiState.publishing,
        replyingBlog = uiState.replyBlog,
        onContentChanged = onContentChanged,
        onPublishClick = onPublishClick,
        onBackClick = onBackClick,
        onAddAccountClick = onAddAccountClick,
        postSettingLabel = {
            PostInteractionSettingLabel(
                modifier = Modifier.padding(top = 1.dp),
                setting = uiState.interactionSetting,
                lists = uiState.list,
                onQuoteChange = onQuoteChange,
                onSettingSelected = onSettingSelected,
                onSettingOptionsSelected = onSettingOptionsSelected,
            )
        },
        bottomPanel = {
            PublishPostFeaturesPanel(
                modifier = Modifier.fillMaxWidth().bottomPaddingAsBottomBar(),
                contentLength = uiState.content.text.length,
                maxContentLimit = uiState.maxCharacters,
                mediaAvailableCount = uiState.remainingImageCount,
                selectedLanguages = uiState.selectedLanguages,
                maxLanguageCount = uiState.maxLanguageCount,
                onMediaSelected = onMediaSelected,
                onLanguageSelected = onLanguageSelected,
                floatingBar = {
                    MentionCandidateBar(
                        uiState = uiState,
                        onMentionClick = onMentionCandidateClick,
                    )
                },
            )
        },
        attachment = { style ->
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
            if (uiState.quoteBlog != null) {
                BlogInQuoting(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    blog = uiState.quoteBlog,
                    style = style.statusStyle,
                )
            }
        },
        allowHashtagInHashtag = true,
    )
}
