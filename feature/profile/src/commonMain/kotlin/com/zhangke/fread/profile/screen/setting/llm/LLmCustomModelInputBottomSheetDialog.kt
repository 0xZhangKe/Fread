package com.zhangke.fread.profile.screen.setting.llm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.rememberTransientModalBottomSheetState
import com.zhangke.fread.common.ai.model.LLMModelConfig
import com.zhangke.fread.common.ai.model.LLMProvider
import com.zhangke.fread.localization.LocalizedString
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LLmCustomModelInputBottomSheetDialog(
    onDismissRequest: () -> Unit,
    onModelAddClick: (LLMModelConfig) -> Unit,
) {
    val sheetState = rememberTransientModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var modelId by rememberSaveable { mutableStateOf("") }
    var baseUrl by rememberSaveable { mutableStateOf("") }
    var versionName by rememberSaveable { mutableStateOf("") }
    var apiKey by rememberSaveable { mutableStateOf("") }

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
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(LocalizedString.llm_config_add_custom_model),
                style = MaterialTheme.typography.titleLarge,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = modelId,
                onValueChange = { modelId = it },
                label = { Text(stringResource(LocalizedString.llm_config_model_id_label)) },
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = baseUrl,
                onValueChange = { baseUrl = it },
                label = { Text(stringResource(LocalizedString.llm_config_base_url_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = versionName,
                onValueChange = { versionName = it },
                label = { Text(stringResource(LocalizedString.llm_config_version_name_label)) },
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text(stringResource(LocalizedString.llm_config_api_key_label)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = modelId.isNotBlank() &&
                    baseUrl.isNotBlank() &&
                    versionName.isNotBlank() &&
                    apiKey.isNotBlank(),
                onClick = {
                    val trimmedModelId = modelId.trim()
                    onModelAddClick(
                        LLMModelConfig(
                            provider = LLMProvider(
                                id = trimmedModelId,
                                displayName = trimmedModelId,
                                baseUrl = baseUrl.trim(),
                            ),
                            versionName = versionName.trim(),
                            apiKey = apiKey.trim()
                                .removePrefix("Bearer ")
                                .removePrefix("bearer "),
                        ),
                    )
                    dismissSheet()
                },
            ) {
                Text(text = stringResource(LocalizedString.ok))
            }
        }
    }
}
