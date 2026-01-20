package com.zhangke.fread.activitypub.app.internal.screen.status.post.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.BackHandler
import androidx.compose.ui.unit.dp
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.utils.Locale
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.initLocale
import com.zhangke.framework.utils.languageCode
import com.zhangke.fread.activitypub.app.internal.model.CustomEmoji
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusUiState
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostFeaturesPanel
import com.zhangke.fread.commonbiz.shared.screen.publish.SensitiveIconButton
import com.zhangke.fread.commonbiz.shared.screen.publish.bottomPaddingAsBottomBar
import com.zhangke.fread.status.ui.BlogAuthorAvatar

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun PostStatusBottomBar(
    uiState: PostStatusUiState,
    onSensitiveClick: () -> Unit,
    onPollClicked: () -> Unit,
    onMediaSelected: (List<PlatformUri>) -> Unit,
    onLanguageSelected: (Locale) -> Unit,
    onEmojiPick: (CustomEmoji) -> Unit,
    onMentionClick: (ActivityPubAccountEntity) -> Unit,
    onDeleteEmojiClick: () -> Unit,
) {
    var showEmojiPicker by remember { mutableStateOf(false) }
    PublishPostFeaturesPanel(
        modifier = Modifier.fillMaxWidth().bottomPaddingAsBottomBar(),
        contentLength = uiState.content.text.length,
        maxContentLimit = uiState.rules.maxCharacters,
        mediaAvailableCount = uiState.rules.maxMediaCount,
        onMediaSelected = onMediaSelected,
        selectedLanguages = listOf(uiState.language.languageCode),
        mediaSelectEnabled = !uiState.isQuotingBlogMode,
        maxLanguageCount = 1,
        onLanguageSelected = { onLanguageSelected(initLocale(it.first())) },
        actions = {
            SimpleIconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 4.dp),
                onClick = onPollClicked,
                enabled = !uiState.isQuotingBlogMode,
                imageVector = Icons.Default.Poll,
                contentDescription = "Add Poll",
            )
            if (uiState.emojiList.isNotEmpty()) {
                SimpleIconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 4.dp),
                    onClick = { showEmojiPicker = true },
                    imageVector = Icons.Default.EmojiEmotions,
                    contentDescription = "Pick Emoji",
                )
            }
            SensitiveIconButton(onSensitiveClick = onSensitiveClick)
        },
        floatingBar = { BottomBarMentions(uiState, onMentionClick) },
    )

    val bottomPaddingByIme = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val bottomEmojiBarHeight = 48.dp
    AnimatedVisibility(
        visible = showEmojiPicker,
        modifier = Modifier.padding(bottom = bottomPaddingByIme),
    ) {
        BackHandler(showEmojiPicker) {
            showEmojiPicker = false
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            CustomEmojiPicker(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = bottomEmojiBarHeight),
                emojiList = uiState.emojiList,
                onEmojiPick = onEmojiPick,
            )
            Surface(
                modifier = Modifier
                    .height(bottomEmojiBarHeight)
                    .align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    SimpleIconButton(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .align(Alignment.CenterStart),
                        onClick = { showEmojiPicker = false },
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Hide keyboard",
                    )
                    SimpleIconButton(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.CenterEnd),
                        onClick = onDeleteEmojiClick,
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete emoji",
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBarMentions(
    uiState: PostStatusUiState,
    onMentionClick: (ActivityPubAccountEntity) -> Unit,
) {
    val mentionState = uiState.mentionState
    if (mentionState.isIdle || mentionState.isFailed) return
    Spacer(modifier = Modifier.height(8.dp))
    if (mentionState.isLoading) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
            items(10) {
                MentionedItem(null, onMentionClick)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    } else if (mentionState.isSuccess) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
            items(mentionState.requireSuccessData()) {
                MentionedItem(it, onMentionClick)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun MentionedItem(
    account: ActivityPubAccountEntity?,
    onMentionClick: (ActivityPubAccountEntity) -> Unit,
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(12),
                color = MaterialTheme.colorScheme.outline,
            )
            .clickable(account != null) { account?.let(onMentionClick) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(6.dp))
        BlogAuthorAvatar(
            modifier = Modifier.size(18.dp),
            imageUrl = account?.avatar,
        )
        Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .widthIn(min = 80.dp)
                .freadPlaceholder(account?.acct.isNullOrEmpty()),
            text = account?.acct.orEmpty(),
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.width(6.dp))
    }
}
