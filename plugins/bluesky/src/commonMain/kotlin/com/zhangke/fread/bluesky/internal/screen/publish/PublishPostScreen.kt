package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.zhangke.framework.nav.popIfNotRoot
import com.zhangke.framework.nav.replaceTopOrAdd
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostFeaturesPanel
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostScaffold
import com.zhangke.fread.commonbiz.shared.screen.publish.bottomPaddingAsBottomBar
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.PostInteractionSettingLabel
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.MultiAccountPublishingScreenKey
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.ReplySetting
import com.zhangke.fread.status.ui.common.DetectedLinkCard
import com.zhangke.fread.status.ui.common.LinkPreviewCard
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
        onBackClick = backStack::popIfNotRoot,
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
                backStack.replaceTopOrAdd(key)
            }
        },
        onMentionCandidateClick = viewModel::onMentionCandidateClick,
        onLinkPreviewCardRemoveClicked = viewModel::onLinkPreviewCardRemoveClicked,
        onAcceptSuggestedLanguage = viewModel::onAcceptSuggestedLanguage,
        onDismissSuggestedLanguage = viewModel::onDismissSuggestedLanguage,
    )
    ConsumeSnackbarFlow(snackBarHostState, viewModel.snackBarMessageFlow)
    ConsumeFlow(viewModel.finishPageFlow) {
        backStack.popIfNotRoot()
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
    onLinkPreviewCardRemoveClicked: () -> Unit,
    onAcceptSuggestedLanguage: () -> Unit,
    onDismissSuggestedLanguage: () -> Unit,
) {
    PublishPostScaffold(
        account = uiState.account,
        snackBarHostState = snackBarHostState,
        content = uiState.content,
        showSwitchAccountIcon = false,
        publishEnabled = uiState.publishEnabled,
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
            if (uiState.detectedLinkCard != null && uiState.detectedLinkCard !is DetectedLinkCard.Deleted) {
                LinkPreviewCard(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    card = uiState.detectedLinkCard,
                    onRemoveClick = onLinkPreviewCardRemoveClicked,
                )
            }
            uiState.suggestedLanguage?.let { suggestion ->
                SuggestedLanguageBanner(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    languageTag = suggestion,
                    onAccept = onAcceptSuggestedLanguage,
                    onDismiss = onDismissSuggestedLanguage,
                )
            }
        },
        allowHashtagInHashtag = true,
    )
}

@Composable
private fun SuggestedLanguageBanner(
    modifier: Modifier,
    languageTag: String,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    val displayName = remember(languageTag) { displayNameOf(languageTag) }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                modifier = Modifier.weight(1F),
                text = "Are you writing in $displayName?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = onAccept) {
                Text("Yes")
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Best-effort mapping of a BCP-47 tag to a human-readable name. */
private fun displayNameOf(tag: String): String = when (tag.lowercase().substringBefore('-')) {
    "en" -> "English"
    "es" -> "Spanish"
    "pt" -> "Portuguese"
    "fr" -> "French"
    "de" -> "German"
    "it" -> "Italian"
    "ja" -> "Japanese"
    "ko" -> "Korean"
    "zh" -> "Chinese"
    "ru" -> "Russian"
    "ar" -> "Arabic"
    "nl" -> "Dutch"
    "pl" -> "Polish"
    "tr" -> "Turkish"
    "sv" -> "Swedish"
    "no" -> "Norwegian"
    "da" -> "Danish"
    "fi" -> "Finnish"
    "cs" -> "Czech"
    "el" -> "Greek"
    "he" -> "Hebrew"
    "hi" -> "Hindi"
    "id" -> "Indonesian"
    "th" -> "Thai"
    "uk" -> "Ukrainian"
    "vi" -> "Vietnamese"
    else -> tag
}
