package com.zhangke.fread.profile.screen.setting.ai

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.profile.screen.setting.SettingItem
import com.zhangke.fread.profile.screen.setting.alttext.AltTextSettingsNavKey
import com.zhangke.fread.profile.screen.setting.llm.LLmConfigNavKey
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
object AISettingsNavKey : NavKey

@Composable
fun AISettingsScreen(viewModel: AISettingsViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()

    AISettingsContent(
        uiState = uiState,
        onBackClick = backStack::removeLastOrNull,
        onAltTextClick = {
            backStack.add(AltTextSettingsNavKey)
        },
        onLlmConfigClick = {
            backStack.add(LLmConfigNavKey)
        },
    )
}

@Composable
private fun AISettingsContent(
    uiState: AISettingsUiState,
    onBackClick: () -> Unit,
    onAltTextClick: () -> Unit,
    onLlmConfigClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.setting_group_ai),
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            SettingItem(
                icon = Icons.Default.Description,
                title = stringResource(LocalizedString.alt_text_settings_title),
                subtitle = stringResource(LocalizedString.alt_text_settings_prompt_label),
                onClick = onAltTextClick,
            )
            SettingItem(
                icon = Icons.AutoMirrored.Outlined.Chat,
                title = stringResource(LocalizedString.llm_config_settings_title),
                subtitle = uiState.currentLLMModel
                    ?.let { "${it.provider.displayName}/${it.versionName}" }
                    ?.let {
                        stringResource(LocalizedString.llm_config_settings_subtitle_configured, it)
                    }
                    ?: stringResource(LocalizedString.llm_config_settings_subtitle_not_configured),
                onClick = onLlmConfigClick,
            )
        }
    }
}
