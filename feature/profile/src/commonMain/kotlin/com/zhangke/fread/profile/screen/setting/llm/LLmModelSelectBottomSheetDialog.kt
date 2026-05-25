package com.zhangke.fread.profile.screen.setting.llm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.rememberTransientModalBottomSheetState
import com.zhangke.fread.common.ai.model.LLMModelConfig
import com.zhangke.fread.common.ai.model.LLMProvider
import com.zhangke.fread.common.ai.model.versions
import com.zhangke.fread.localization.LocalizedString
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LLmModelSelectBottomSheetDialog(
    onDismissRequest: () -> Unit,
    onModelAddClick: (LLMModelConfig) -> Unit,
    onAddCustomModelClick: () -> Unit,
) {
    val sheetState = rememberTransientModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var expandedProviderId by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingModel by remember { mutableStateOf<PendingModel?>(null) }

    fun dismissSheet() {
        coroutineScope.launch {
            sheetState.hide()
            onDismissRequest()
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = ::dismissSheet,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7F),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = stringResource(LocalizedString.llm_config_select_model_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                TextButton(onClick = onAddCustomModelClick) {
                    Text(text = stringResource(LocalizedString.llm_config_add_custom_model))
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp),
            ) {
                LLMProvider.allSupportedProvider.forEach { provider ->
                    ProviderItem(
                        provider = provider,
                        expanded = expandedProviderId == provider.id,
                        onClick = {
                            expandedProviderId = if (expandedProviderId == provider.id) {
                                null
                            } else {
                                provider.id
                            }
                        },
                        onVersionClick = { version ->
                            pendingModel = PendingModel(provider = provider, versionName = version)
                        },
                    )
                }
            }
        }
    }

    pendingModel?.let { model ->
        ApiKeyInputDialog(
            onDismissRequest = { pendingModel = null },
            onConfirm = { apiKey ->
                onModelAddClick(
                    LLMModelConfig(
                        provider = model.provider,
                        versionName = model.versionName,
                        apiKey = apiKey.removePrefix("Bearer ").removePrefix("bearer "),
                    ),
                )
                pendingModel = null
                dismissSheet()
            },
        )
    }
}

@Composable
private fun ProviderItem(
    provider: LLMProvider,
    expanded: Boolean,
    onClick: () -> Unit,
    onVersionClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1F),
                text = provider.displayName,
                style = MaterialTheme.typography.titleMedium,
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
            )
        }
        if (expanded) {
            provider.versions.forEach { version ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onVersionClick(version) }
                        .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 10.dp),
                    text = version,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun ApiKeyInputDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var apiKey by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text(stringResource(LocalizedString.alt_text_settings_api_key_label)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
        },
        confirmButton = {
            TextButton(
                enabled = apiKey.isNotBlank(),
                onClick = { onConfirm(apiKey.trim()) },
            ) {
                Text(text = stringResource(LocalizedString.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(LocalizedString.cancel))
            }
        },
    )
}

private data class PendingModel(
    val provider: LLMProvider,
    val versionName: String,
)
