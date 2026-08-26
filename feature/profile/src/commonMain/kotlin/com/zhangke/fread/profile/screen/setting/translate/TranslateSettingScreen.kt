package com.zhangke.fread.profile.screen.setting.translate

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
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.commonbiz.shared.screen.SelectLanguageScreenNavKey
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.profile.screen.setting.SettingItemWithSwitch
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
        onSelectLanguageClick = {
            backStack.add(SelectLanguageScreenNavKey())
        },
        onAiTranslateEnableChanged = viewModel::onAiTranslateEnableChanged,
    )
    ConsumeFlow(SelectLanguageScreenNavKey.selectedFlow.flow) { list ->
        list.firstOrNull()?.let { viewModel.onLanguageSelected(it) }
    }
}

@Composable
private fun TranslateSettingContent(
    uiState: TranslateSettingUiState,
    onBackClick: () -> Unit,
    onSelectLanguageClick: () -> Unit,
    onAiTranslateEnableChanged: (Boolean) -> Unit,
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
            SettingItemWithSwitch(
                icon = Icons.Default.SmartToy,
                title = stringResource(LocalizedString.setting_item_ai_translation_title),
                subtitle = stringResource(LocalizedString.setting_item_ai_translation_subtitle),
                checked = uiState.enabled,
                onCheckedChangeRequest = onAiTranslateEnableChanged,
            )
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
                    modifier = Modifier.padding(start = 8.dp, end = 16.dp).size(16.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                )
            }
        }
    }
}
