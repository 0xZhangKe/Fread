package com.zhangke.fread.bluesky.internal.screen.add

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LoadingDialog
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.common.utils.LocalToastHelper
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Serializable
data class AddBlueskyContentScreenNavKey(
    val baseUrl: FormalBaseUrl? = null,
    val loginMode: Boolean = false,
    val avatar: String? = null,
    val displayName: String? = null,
    val handle: String? = null,
) : NavKey

@Composable
fun AddBlueskyContentScreen(viewModel: AddBlueskyContentViewModel) {
    val toastHelper = LocalToastHelper.current
    val backStack = LocalNavBackStack.currentOrThrow
    val snackBarHostState = rememberSnackbarHostState()
    val uiState by viewModel.uiState.collectAsState()
    AddBlueskyContentContent(
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onHostingChange = viewModel::onHostingChange,
        onUserNameChange = viewModel::onUserNameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onFactorTokenChange = viewModel::onFactorTokenChange,
        onBackClick = backStack::removeLastOrNull,
        onLoginClick = viewModel::onLoginClick,
    )
    LoadingDialog(loading = uiState.logging, onDismissRequest = viewModel::onCancelLogin)
    ConsumeSnackbarFlow(snackBarHostState, viewModel.snackBarMessage)
    ConsumeFlow(viewModel.loginSuccessFlow) {
        toastHelper.showToast(getString(LocalizedString.addContentSuccessSnackbar))
        backStack.removeLastOrNull()
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
    onLoginClick: () -> Unit,
) {
    val avatarSize = 48.dp
    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.bsky_add_content_title),
                onBackClick = onBackClick,
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .padding(start = 16.dp, end = 16.dp),
        ) {
            if (uiState.loginToSpecAccount) {
                AccountInfoCard(
                    modifier = Modifier.padding(top = 18.dp),
                    avatar = uiState.avatar.orEmpty(),
                    avatarSize = avatarSize,
                    displayName = uiState.displayName.orEmpty(),
                    handle = uiState.handle!!,
                )

                val lineColor = MaterialTheme.colorScheme.outlineVariant

                Canvas(
                    Modifier.height(68.dp)
                        .padding(
                            start = 16.dp + avatarSize / 2,
                            top = 8.dp,
                        ).width(1.dp),
                ) {
                    drawLine(
                        color = lineColor,
                        strokeWidth = 1.dp.toPx(),
                        cap = StrokeCap.Round,
                        start = Offset(0F, 0f),
                        end = Offset(0F, size.height),
                    )
                }
            } else {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    value = uiState.hosting,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                        )
                    },
                    onValueChange = onHostingChange,
                    label = {
                        Text(stringResource(LocalizedString.bsky_add_content_hosting_provider))
                    },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = uiState.username,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AlternateEmail,
                            contentDescription = null,
                        )
                    },
                    onValueChange = onUserNameChange,
                    label = {
                        Text(stringResource(LocalizedString.bsky_add_content_user_name))
                    },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.password,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = onPasswordChange,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(stringResource(LocalizedString.bsky_add_content_password))
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
                        Text(stringResource(LocalizedString.bsky_add_content_factor_token))
                    },
                    singleLine = true,
                )
            }
            Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Button(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                ) {
                    Text(stringResource(LocalizedString.login))
                }
            }
        }
    }
}

@Composable
private fun AccountInfoCard(
    modifier: Modifier,
    avatarSize: Dp,
    avatar: String,
    displayName: String,
    handle: String,
) {
    Box(modifier = modifier) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BlogAuthorAvatar(
                    modifier = Modifier.size(avatarSize),
                    imageUrl = avatar,
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp).weight(1F),
                ) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = handle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
