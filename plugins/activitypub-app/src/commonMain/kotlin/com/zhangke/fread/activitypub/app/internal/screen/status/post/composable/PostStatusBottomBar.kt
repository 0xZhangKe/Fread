package com.zhangke.fread.activitypub.app.internal.screen.status.post.composable

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.utils.buildPickVisualMediaRequest
import com.zhangke.framework.utils.rememberPickVisualMediaLauncher
import com.zhangke.fread.activitypub.app.internal.model.CustomEmoji
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusUiState
import com.zhangke.fread.commonbiz.shared.screen.SelectLanguageScreen
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.ic_post_status_spoiler
import org.jetbrains.compose.resources.painterResource
import java.util.Locale

@OptIn(InternalVoyagerApi::class)
@Composable
internal fun PostStatusBottomBar(
    uiState: PostStatusUiState,
    onSensitiveClick: () -> Unit,
    onPollClicked: () -> Unit,
    onMediaSelected: (List<Uri>) -> Unit,
    onLanguageSelected: (Locale) -> Unit,
    onEmojiPick: (CustomEmoji) -> Unit,
    onMentionClick: (ActivityPubAccountEntity) -> Unit,
    onDeleteEmojiClick: () -> Unit,
) {
    val bottomPaddingByIme = WindowInsets.ime
        .asPaddingValues()
        .calculateBottomPadding()
    val modifier = if (bottomPaddingByIme > 0.dp) {
        Modifier.padding(bottom = bottomPaddingByIme)
    } else {
        Modifier.navigationBarsPadding()
    }
    var showEmojiPicker by remember {
        mutableStateOf(false)
    }
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BottomBarMentions(uiState, onMentionClick)
            Row {
                SelectedMediaIconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp),
                    onMediaSelected = onMediaSelected,
                    allowedSelectCount = uiState.allowedSelectCount,
                )
                SimpleIconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp),
                    onClick = onPollClicked,
                    imageVector = Icons.Default.Poll,
                    contentDescription = "Add Poll",
                )
                if (uiState.emojiList.isNotEmpty()) {
                    SimpleIconButton(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 16.dp),
                        onClick = { showEmojiPicker = true },
                        imageVector = Icons.Default.EmojiEmotions,
                        contentDescription = "Pick Emoji",
                    )
                }
                SimpleIconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp),
                    onClick = onSensitiveClick,
                    painter = painterResource(Res.drawable.ic_post_status_spoiler),
                    contentDescription = "Sensitive content",
                )
                SelectLanguageIconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp),
                    onLanguageSelected = onLanguageSelected,
                )
                Spacer(modifier = Modifier.weight(1F))
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 16.dp),
                    text = uiState.allowedInputCount.toString(),
                )
            }
            val bottomEmojiBarHeight = 48.dp
            AnimatedVisibility(
                visible = showEmojiPicker,
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
    }
}

@Composable
private fun SelectedMediaIconButton(
    modifier: Modifier,
    allowedSelectCount: Int,
    onMediaSelected: (List<Uri>) -> Unit,
) {
    val launcher = rememberPickVisualMediaLauncher(
        maxItems = allowedSelectCount,
        onResult = onMediaSelected,
    )
    SimpleIconButton(
        modifier = modifier,
        onClick = {
            launcher?.launch(buildPickVisualMediaRequest())
        },
        imageVector = Icons.Default.Image,
        contentDescription = "Add Image",
    )
}

@Composable
private fun SelectLanguageIconButton(
    modifier: Modifier,
    onLanguageSelected: (Locale) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    Box(modifier = modifier) {
        SimpleIconButton(
            onClick = {
                navigator.push(
                    SelectLanguageScreen(
                        onSelected = onLanguageSelected
                    )
                )
            },
            imageVector = Icons.Default.Language,
            contentDescription = "Choose language",
        )
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
