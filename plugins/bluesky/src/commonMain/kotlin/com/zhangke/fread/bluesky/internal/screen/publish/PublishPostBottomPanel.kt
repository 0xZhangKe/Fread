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
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.pick.PickVisualMediaLauncherContainer
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.getDisplayName
import com.zhangke.framework.utils.initLocale
import com.zhangke.fread.commonbiz.shared.screen.SelectLanguageScreen
import com.zhangke.fread.status.ui.common.RemainingTextStatus

@Composable
fun PublishBottomPanel(
    uiState: PublishPostUiState,
    onMediaSelected: (List<PlatformUri>) -> Unit,
    selectedLanguages: List<String>,
    maxLanguageCount: Int,
    onLanguageSelected: (List<String>) -> Unit,
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
                .padding(start = 8.dp, end = 16.dp),
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
                selectedLanguages = selectedLanguages,
                maxLanguageCount = maxLanguageCount,
            )

            RemainingTextStatus(
                modifier = Modifier,
                maxCount = uiState.maxCharacters,
                contentLength = uiState.content.text.length,
            )
        }
    }
}

@Composable
private fun SelectLanguageIconButton(
    modifier: Modifier,
    selectedLanguages: List<String>,
    maxLanguageCount: Int,
    onLanguageSelected: (List<String>) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    fun selectLanguage() {
        navigator.push(
            SelectLanguageScreen(
                selectedLanguages = selectedLanguages,
                maxSelectCount = maxLanguageCount,
                multipleSelection = true,
                onSelected = onLanguageSelected,
            )
        )
    }
    Box(modifier = modifier) {
        if (selectedLanguages.isEmpty()) {
            SimpleIconButton(
                onClick = { selectLanguage() },
                imageVector = Icons.Default.Language,
                contentDescription = "Choose language",
            )
        } else {
            TextButton(
                onClick = { selectLanguage() },
            ) {
                val languages = remember(selectedLanguages) {
                    selectedLanguages.map { initLocale(it) }
                        .joinToString { it.getDisplayName(it) }
                }
                Text(
                    text = languages,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
