package com.zhangke.utopia.feeds.pages.post

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.EmojiFlags
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.utils.rememberPickVisualMediaLauncher
import com.zhangke.utopia.commonbiz.shared.screen.SelectLanguageScreen
import java.util.Locale

@Composable
internal fun PostStatusBottomBar(
    height: Dp,
    uiState: PostStatusUiState,
    onSensitiveClick: () -> Unit,
    onMediaSelected: (List<Uri>) -> Unit,
    onLanguageSelected: (Locale) -> Unit,
) {
    val bottomPaddingByIme = WindowInsets.ime
        .asPaddingValues()
        .calculateBottomPadding()
    val modifier = if (bottomPaddingByIme > 0.dp) {
        Modifier.padding(bottom = bottomPaddingByIme)
    } else {
        Modifier.navigationBarsPadding()
    }
    Surface(modifier = modifier.height(height)) {
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
                onClick = { /*TODO*/ },
                imageVector = Icons.Default.Poll,
                contentDescription = "Add Poll",
            )
            SimpleIconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp),
                onClick = { /*TODO*/ },
                imageVector = Icons.Default.EmojiFlags,
                contentDescription = "Add Emoji",
            )
            SimpleIconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp),
                onClick = onSensitiveClick,
                imageVector = Icons.Default.AddAlert,
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
                text = "1000",
            )
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
            launcher?.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    .build()
            )
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
