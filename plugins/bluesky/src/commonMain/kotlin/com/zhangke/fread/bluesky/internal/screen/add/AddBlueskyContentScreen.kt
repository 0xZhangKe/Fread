package com.zhangke.fread.bluesky.internal.screen.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LoadingDialog
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.Res
import com.zhangke.fread.bluesky.bsky_add_content_factor_token
import com.zhangke.fread.bluesky.bsky_add_content_hosting_provider
import com.zhangke.fread.bluesky.bsky_add_content_password
import com.zhangke.fread.bluesky.bsky_add_content_title
import com.zhangke.fread.bluesky.bsky_add_content_user_name
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.login
import com.zhangke.fread.framework.skip
import org.jetbrains.compose.resources.stringResource

class AddBlueskyContentScreen(
    private val baseUrl: FormalBaseUrl,
    private val loginMode: Boolean = false,
) : BaseScreen() {

    override val key: ScreenKey
        get() = baseUrl.toString() + loginMode

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val snackBarHostState = rememberSnackbarHostState()
        val viewModel =
            getViewModel<AddBlueskyContentViewModel, AddBlueskyContentViewModel.Factory> {
                it.create(baseUrl, loginMode)
            }
        val uiState by viewModel.uiState.collectAsState()
        AddBlueskyContentContent(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onHostingChange = viewModel::onHostingChange,
            onUserNameChange = viewModel::onUserNameChange,
            onPasswordChange = viewModel::onPasswordChange,
            onFactorTokenChange = viewModel::onFactorTokenChange,
            onBackClick = navigator::pop,
            onSkipClick = viewModel::onSkipClick,
            onLoginClick = viewModel::onLoginClick,
        )
        LoadingDialog(loading = uiState.logging, onDismissRequest = viewModel::onCancelLogin)
        ConsumeSnackbarFlow(snackBarHostState, viewModel.snackBarMessage)
        ConsumeFlow(viewModel.finishPageFlow) {
            navigator.pop()
        }
    }

    @Composable
    private fun AddBlueskyContentContent(
        uiState: AddBlueskyContentUiState,
        snackBarHostState: SnackbarHostState,
        onHostingChange: (String) -> Unit,
        onUserNameChange: (String) -> Unit,
        onPasswordChange: (String) -> Unit,
        onFactorTokenChange: (String) -> Unit,
        onBackClick: () -> Unit,
        onSkipClick: () -> Unit,
        onLoginClick: () -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize().imePadding(),
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.bsky_add_content_title),
                    onBackClick = onBackClick,
                )
            },
            snackbarHost = {
                SnackbarHost(snackBarHostState)
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    value = uiState.hosting,
                    readOnly = loginMode,
                    onValueChange = onHostingChange,
                    label = {
                        Text(stringResource(Res.string.bsky_add_content_hosting_provider))
                    },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = uiState.username,
                    onValueChange = onUserNameChange,
                    label = {
                        Text(stringResource(Res.string.bsky_add_content_user_name))
                    },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = uiState.password,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = onPasswordChange,
                    label = {
                        Text(stringResource(Res.string.bsky_add_content_password))
                    },
                    singleLine = true,
                )
                if (uiState.authFactorRequired) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        value = uiState.factorToken,
                        onValueChange = onFactorTokenChange,
                        label = {
                            Text(stringResource(Res.string.bsky_add_content_factor_token))
                        },
                        singleLine = true,
                    )
                }
                Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    if (!loginMode) {
                        Button(
                            modifier = Modifier.align(Alignment.CenterStart),
                            onClick = onSkipClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                        ) {
                            Text(stringResource(com.zhangke.fread.framework.Res.string.skip))
                        }
                    }
                    Button(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = onLoginClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                        enabled = uiState.canLogin,
                    ) {
                        Text(stringResource(com.zhangke.fread.commonbiz.Res.string.login))
                    }
                }
            }
        }
    }
}
