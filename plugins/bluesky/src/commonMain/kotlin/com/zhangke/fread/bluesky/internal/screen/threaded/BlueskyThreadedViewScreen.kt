package com.zhangke.fread.bluesky.internal.screen.threaded

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class BlueskyThreadedViewScreenNavKey(
    val locator: PlatformLocator,
    val postUri: String,
    val opDid: String,
) : NavKey

@Composable
fun BlueskyThreadedViewScreen(viewModel: BlueskyThreadedViewViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    val onBack: () -> Unit = { backStack.removeLastOrNull() }

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.threaded_view_screen_title),
                onBackClick = onBack,
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            Box(
                modifier = Modifier.weight(1F).fillMaxWidth(),
            ) {
                when (val state = uiState) {
                    BlueskyThreadedViewUiState.Loading -> LoadingState()
                    BlueskyThreadedViewUiState.Empty -> EmptyState()
                    is BlueskyThreadedViewUiState.Loaded -> LoadedState(state.text)
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                onClick = onBack,
            ) {
                Text(text = stringResource(LocalizedString.threaded_view_back))
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(LocalizedString.threaded_view_assembling),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = stringResource(LocalizedString.threaded_view_empty),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LoadedState(text: String) {
    SelectionContainer(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
