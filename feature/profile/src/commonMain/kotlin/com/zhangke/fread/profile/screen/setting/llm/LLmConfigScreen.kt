package com.zhangke.fread.profile.screen.setting.llm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.localization.LocalizedString
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
object LLmConfigNavKey : NavKey

@Composable
fun LLmConfigScreen(viewModel: LLmConfigViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.llm_config_settings_title),
                onBackClick = { backStack.removeLastOrNull() },
            )
        },
    ) { innerPadding ->
        var selectModelDialogVisible by remember { mutableStateOf(false) }
        var customModelDialogVisible by remember { mutableStateOf(false) }
        var pendingDeleteConfig by remember { mutableStateOf<LLmConfigItemUiState?>(null) }
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            uiState.configs.forEach { config ->
                LlmConfigItem(
                    config = config,
                    onSelectedChange = viewModel::onSelectedChange,
                    onDeleteClick = { pendingDeleteConfig = it },
                )
            }
            AddModelLine(
                onAddModelClick = { selectModelDialogVisible = true },
            )
        }
        if (selectModelDialogVisible) {
            LLmModelSelectBottomSheetDialog(
                onDismissRequest = { selectModelDialogVisible = false },
                onModelAddClick = {
                    viewModel.onAddModelConfig(it)
                    selectModelDialogVisible = false
                },
                onAddCustomModelClick = {
                    selectModelDialogVisible = false
                    customModelDialogVisible = true
                },
            )
        }

        if (customModelDialogVisible) {
            LLmCustomModelInputBottomSheetDialog(
                onDismissRequest = { customModelDialogVisible = false },
                onModelAddClick = {
                    viewModel.onAddModelConfig(it)
                    customModelDialogVisible = false
                },
            )
        }

        pendingDeleteConfig?.let { config ->
            AlertConfirmDialog(
                content = stringResource(LocalizedString.llm_config_delete_model_confirm),
                onDismissRequest = { pendingDeleteConfig = null },
                onConfirm = { viewModel.onDeleteModelConfig(config) },
            )
        }
    }
}

@Composable
private fun LlmConfigItem(
    config: LLmConfigItemUiState,
    onSelectedChange: (LLmConfigItemUiState) -> Unit,
    onDeleteClick: (LLmConfigItemUiState) -> Unit,
) {
    var apiKeyVisible by rememberSaveable(config.providerId, config.versionName) {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!config.selected) {
                    onSelectedChange(config)
                }
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1F),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = config.modelName,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = config.versionName,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                RadioButton(
                    selected = config.selected,
                    onClick = {
                        if (!config.selected) {
                            onSelectedChange(config)
                        }
                    },
                )
                IconButton(
                    onClick = { onDeleteClick(config) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(LocalizedString.llm_config_delete_model),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
            ApiKeyRow(
                apiKey = config.apiKey,
                visible = apiKeyVisible,
                onVisibleChange = { apiKeyVisible = !apiKeyVisible },
            )
        }
    }
}

@Composable
private fun ApiKeyRow(
    apiKey: String,
    visible: Boolean,
    onVisibleChange: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1F),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(LocalizedString.alt_text_settings_api_key_label),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = if (visible) apiKey else maskApiKey(apiKey),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        TextButton(onClick = onVisibleChange) {
            Text(
                if (visible) {
                    stringResource(LocalizedString.alt_text_settings_api_key_hide)
                } else {
                    stringResource(LocalizedString.alt_text_settings_api_key_show)
                },
            )
        }
    }
}

@Composable
private fun AddModelLine(onAddModelClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            onClick = onAddModelClick,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                TextButton(
                    onClick = onAddModelClick,
                ) {
                    Text(stringResource(LocalizedString.llm_config_add_model))
                }
            }
        }
    }
}

private fun maskApiKey(apiKey: String): String {
    if (apiKey.isBlank()) return ""
    return "•".repeat(8)
}
