package com.zhangke.fread.commonbiz.shared.screen.publish

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.zhangke.fread.statusui.ic_post_status_spoiler
import org.jetbrains.compose.resources.painterResource

@Composable
fun Modifier.bottomPaddingAsBottomBar(): Modifier {
    val bottomPaddingByIme = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    return if (bottomPaddingByIme > 0.dp) {
        this.padding(bottom = bottomPaddingByIme)
    } else {
        this.navigationBarsPadding()
    }
}

@Composable
fun PublishPostFeaturesPanel(
    modifier: Modifier,
    contentLength: Int,
    maxContentLimit: Int,
    mediaAvailableCount: Int,
    onMediaSelected: (List<PlatformUri>) -> Unit,
    selectedLanguages: List<String>,
    maxLanguageCount: Int,
    onLanguageSelected: (List<String>) -> Unit,
    mediaSelectEnabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    actions: @Composable RowScope.() -> Unit = {},
    floatingBar: @Composable () -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = containerColor,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            floatingBar.invoke()
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PickVisualMediaLauncherContainer(
                    onResult = onMediaSelected,
                    maxItems = mediaAvailableCount,
                ) {
                    val enabled = mediaSelectEnabled && mediaAvailableCount > 0
                    SimpleIconButton(
                        modifier = Modifier,
                        onClick = { launchImage() },
                        onLongClick = { launchImageFile() },
                        enabled = enabled,
                        imageVector = Icons.Default.Image,
                        contentDescription = "Add Image",
                    )
                }
                PickVisualMediaLauncherContainer(
                    onResult = onMediaSelected,
                    maxItems = 1,
                ) {
                    val enabled = mediaSelectEnabled && mediaAvailableCount > 0
                    SimpleIconButton(
                        modifier = Modifier,
                        onClick = { launchVideo() },
                        onLongClick = { launchVideoFile() },
                        enabled = enabled,
                        imageVector = Icons.Default.SmartDisplay,
                        contentDescription = "Add Video",
                    )
                }
                actions()
                Spacer(modifier = Modifier.weight(1F))
                SelectLanguageIconButton(
                    modifier = Modifier,
                    onLanguageSelected = onLanguageSelected,
                    selectedLanguages = selectedLanguages,
                    maxLanguageCount = maxLanguageCount,
                )
                RemainingTextStatus(
                    modifier = Modifier,
                    maxCount = maxContentLimit,
                    contentLength = contentLength,
                )
            }
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

@Composable
fun SensitiveIconButton(onSensitiveClick: () -> Unit) {
    IconButton(
        onClick = onSensitiveClick,
        modifier = Modifier.padding(start = 4.dp),
    ) {
        Box(modifier = Modifier.size(29.dp)) {
            Icon(
                modifier = Modifier.size(24.dp).align(Alignment.TopCenter),
                painter = painterResource(com.zhangke.fread.statusui.Res.drawable.ic_post_status_spoiler),
                contentDescription = "Sensitive content",
            )
        }
    }
}
