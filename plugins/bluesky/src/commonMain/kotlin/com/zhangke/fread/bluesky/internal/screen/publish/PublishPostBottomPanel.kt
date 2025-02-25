package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.pick.PickVisualMediaLauncherContainer
import com.zhangke.framework.utils.Locale
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.commonbiz.shared.screen.SelectLanguageScreen
import com.zhangke.fread.status.ui.common.RemainingTextStatus

@Composable
fun PublishBottomPanel(
    uiState: PublishPostUiState,
    onMediaSelected: (List<PlatformUri>) -> Unit,
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
    Surface(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PickVisualMediaLauncherContainer(
                onResult = onMediaSelected,
                maxItems = uiState.remainingImageCount,
            ) {
                SimpleIconButton(
                    modifier = Modifier,
                    onClick = { launchImage() },
                    imageVector = Icons.Default.Image,
                    contentDescription = "Add Image",
                )
            }
            PickVisualMediaLauncherContainer(
                onResult = onMediaSelected,
                maxItems = uiState.remainingImageCount,
            ) {
                SimpleIconButton(
                    modifier = Modifier.padding(start = 16.dp),
                    onClick = { launchVideo() },
                    imageVector = Icons.Default.SmartDisplay,
                    contentDescription = "Add Video",
                )
            }

            Spacer(modifier = Modifier.weight(1F))
            SelectLanguageIconButton(
                modifier = Modifier,
                onLanguageSelected = onLanguageSelected,
            )

            RemainingTextStatus(
                modifier = Modifier.padding(start = 16.dp),
                maxCount = uiState.maxCharacters,
                contentLength = uiState.content.text.length,
            )
        }
    }
}

@Composable
private fun SelectLanguageIconButton(
    modifier: Modifier,
    onLanguageSelected: (Locale) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    Box(modifier = modifier) {
        SimpleIconButton(
            onClick = { navigator.push(SelectLanguageScreen(onSelected = onLanguageSelected)) },
            imageVector = Icons.Default.Language,
            contentDescription = "Choose language",
        )
    }
}
