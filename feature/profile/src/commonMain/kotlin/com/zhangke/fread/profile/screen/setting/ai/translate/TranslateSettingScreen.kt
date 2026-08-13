package com.zhangke.fread.profile.screen.setting.ai.translate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.localization.LocalizedString
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object TranslateSettingNavKey : NavKey

@Composable
fun TranslateSettingScreen() {
    val backStack = LocalNavBackStack.currentOrThrow
    val viewModel: TranslateSettingViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    TranslateSettingContent(
        uiState = uiState,
        onBackClick = backStack::removeLastOrNull,
        onSelectLanguageClick = {},
        onPromptChange = viewModel::onPromptChanged,
    )
}

@Composable
private fun TranslateSettingContent(
    uiState: TranslateSettingUiState,
    onBackClick: () -> Unit,
    onSelectLanguageClick: () -> Unit,
    onPromptChange: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.translation_settings_title),
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clickable(onClick = onSelectLanguageClick)
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(start = 16.dp).size(24.dp),
                    contentDescription = null,
                    imageVector = Icons.Default.Language,
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                        .weight(1F),
                ) {
                    Text(
                        text = stringResource(LocalizedString.translation_settings_post_language_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(LocalizedString.translation_settings_post_language_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    modifier = Modifier,
                    text = uiState.targetLanguage.ifNullOrEmpty {
                        stringResource(LocalizedString.notSetYet)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
                Icon(
                    modifier = Modifier.padding(end = 16.dp).size(16.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                )
            }
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(start = 16.dp).size(24.dp),
                    contentDescription = null,
                    imageVector = Icons.Default.SmartToy,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "Prompt",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Card(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.prompt,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    minLines = 3,
                    onValueChange = onPromptChange,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                    ),
                )
            }
        }
    }
}
