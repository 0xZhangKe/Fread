package com.zhangke.fread.profile.screen.setting.alttext

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.localization.LocalizedString
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
object AltTextSettingsNavKey : NavKey

@Composable
fun AltTextSettingsScreen(viewModel: AltTextSettingsViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.alt_text_settings_title),
                onBackClick = { backStack.removeLastOrNull() },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ApiKeyField(
                value = uiState.apiKey,
                onValueChange = viewModel::onApiKeyChange,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.baseUrl,
                onValueChange = viewModel::onBaseUrlChange,
                label = { Text(stringResource(LocalizedString.alt_text_settings_base_url_label)) },
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.model,
                onValueChange = viewModel::onModelChange,
                label = { Text(stringResource(LocalizedString.alt_text_settings_model_label)) },
                singleLine = true,
            )
            ModelPresetRow(onPick = viewModel::onModelChange)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.prompt,
                onValueChange = viewModel::onPromptChange,
                label = { Text(stringResource(LocalizedString.alt_text_settings_prompt_label)) },
                minLines = 3,
                maxLines = 8,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.maxTokens,
                onValueChange = viewModel::onMaxTokensChange,
                label = { Text(stringResource(LocalizedString.alt_text_settings_max_tokens_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
    }
}

@Composable
private fun ModelPresetRow(onPick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ModelPresetChip(
            label = "Qwen 3.5",
            modelId = "qwen/qwen3.5-122b-a10b",
            container = Color(0xFF7C3AED),
            content = Color.White,
            onPick = onPick,
        )
        ModelPresetChip(
            label = "Gemini 2.5 Flash",
            modelId = "google/gemini-2.5-flash",
            container = Color(0xFF1E88E5),
            content = Color.White,
            onPick = onPick,
        )
        ModelPresetChip(
            label = "Kimi 2.5",
            modelId = "moonshotai/kimi-k2.5",
            container = Color(0xFF424242),
            content = Color.White,
            onPick = onPick,
        )
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.ModelPresetChip(
    label: String,
    modelId: String,
    container: Color,
    content: Color,
    onPick: (String) -> Unit,
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .weight(1F)
            .clip(RoundedCornerShape(8.dp))
            .background(container)
            .clickable { onPick(modelId) }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = content,
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            maxLines = 1,
        )
    }
}

@Composable
private fun ApiKeyField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(LocalizedString.alt_text_settings_api_key_label)) },
        singleLine = true,
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            androidx.compose.material3.TextButton(onClick = { visible = !visible }) {
                Text(if (visible) "Hide" else "Show")
            }
        },
    )
}
