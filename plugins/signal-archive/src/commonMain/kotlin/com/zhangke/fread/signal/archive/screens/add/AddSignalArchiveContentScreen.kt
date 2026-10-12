package com.zhangke.fread.signal.archive.screens.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LoadingDialog
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.popIfNotRoot
import com.zhangke.fread.localization.LocalizedString
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class AddSignalArchiveContentNavKey(val url: String = "") : NavKey

@Composable
fun AddSignalArchiveContentScreen(viewModel: AddSignalArchiveContentViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    val snackbar = rememberSnackbarHostState()
    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.signalArchiveContentName),
                onBackClick = backStack::popIfNotRoot,
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.url,
                onValueChange = viewModel::onUrlChanged,
                label = { Text(stringResource(LocalizedString.signalArchiveAddress)) },
                supportingText = { Text(stringResource(LocalizedString.signalArchiveAddressHint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                singleLine = true,
                enabled = !uiState.saving,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                label = { Text(stringResource(LocalizedString.addFeedsPageFeedsNameLabel)) },
                singleLine = true,
                enabled = !uiState.saving,
            )
            Button(
                onClick = viewModel::onAddClick,
                enabled = !uiState.saving && uiState.url.isNotBlank() && uiState.name.isNotBlank(),
            ) {
                Text(stringResource(LocalizedString.add))
            }
        }
    }
    LoadingDialog(loading = uiState.saving, onDismissRequest = {})
    ConsumeSnackbarFlow(snackbar, viewModel.snackbarMessage)
    ConsumeFlow(viewModel.successFlow) { backStack.popIfNotRoot() }
}
