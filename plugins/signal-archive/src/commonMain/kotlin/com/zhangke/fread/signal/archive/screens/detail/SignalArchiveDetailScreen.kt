package com.zhangke.fread.signal.archive.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.material3.MaterialTheme
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.popIfNotRoot
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.signal.archive.screens.ArchiveMessage
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class SignalArchiveDetailNavKey(
    val baseUrl: String,
    val detailPath: String,
    val title: String,
) : NavKey

@Composable
fun SignalArchiveDetailScreen(viewModel: SignalArchiveDetailViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val browser = LocalActivityBrowserLauncher.current
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()
    val article = state.article
    val foreground = MaterialTheme.colorScheme.onSurface.toArgb()
    val background = MaterialTheme.colorScheme.surface.toArgb()
    val linkColor = MaterialTheme.colorScheme.primary.toArgb()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.signalArchiveDetailTitle),
                onBackClick = backStack::popIfNotRoot,
                actions = {
                    article?.link?.takeIf { it.isNotBlank() }?.let { link ->
                        TextButton(onClick = { scope.launch { browser.launchWebTabInApp(link) } }) {
                            Text(stringResource(LocalizedString.statusUiInteractionOpenInBrowser))
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (article != null) {
                val document = remember(article, foreground, background, linkColor) {
                    signalArchiveArticleDocument(article, foreground, background, linkColor)
                }
                SignalArchiveArticleBody(
                    html = document,
                    modifier = Modifier.fillMaxWidth().weight(1F),
                    onLinkClick = { link -> scope.launch { browser.launchWebTabInApp(link) } },
                )
            } else {
                ArchiveMessage(loading = state.loading, failed = state.failed, pageFailed = true, onRetry = viewModel::load)
            }
        }
    }
}
