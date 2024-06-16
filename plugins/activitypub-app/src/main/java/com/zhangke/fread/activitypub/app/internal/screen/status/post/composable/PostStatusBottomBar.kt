package com.zhangke.fread.activitypub.app.internal.screen.status.post.composable

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.utils.buildPickVisualMediaRequest
import com.zhangke.framework.utils.rememberPickVisualMediaLauncher
import com.zhangke.fread.activitypub.app.internal.model.CustomEmoji
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusUiState
import com.zhangke.fread.commonbiz.shared.screen.SelectLanguageScreen
import java.util.Locale

@Composable
internal fun PostStatusBottomBar(
    height: Dp,
    uiState: PostStatusUiState,
    onSensitiveClick: () -> Unit,
    onPollClicked: () -> Unit,
    onMediaSelected: (List<Uri>) -> Unit,
    onLanguageSelected: (Locale) -> Unit,
    onEmojiPick: (CustomEmoji) -> Unit,
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
    Column(modifier = modifier) {
        Surface(modifier = Modifier.height(height)) {
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
                    painter = painterResource(com.zhangke.fread.statusui.R.drawable.ic_post_status_spoiler),
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
        }
        val bottomEmojiBarHeight = 48.dp
        AnimatedVisibility(
            visible = showEmojiPicker,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
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
//                        SimpleIconButton(
//                            modifier = Modifier
//                                .padding(end = 16.dp)
//                                .align(Alignment.CenterEnd),
//                            onClick = onDeleteEmojiClick,
//                            imageVector = Icons.Default.Close,
//                            contentDescription = "Delete emoji",
//                        )
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
