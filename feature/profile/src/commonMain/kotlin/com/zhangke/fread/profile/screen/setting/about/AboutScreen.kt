package com.zhangke.fread.profile.screen.setting.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.toast.toast
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.common.handler.LocalTextHandler
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.ic_fread_logo
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.ui.update.AppUpdateDialog
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class AboutScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<AboutViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val snackBarState = rememberSnackbarHostState()
        AboutScreenContent(
            uiState = uiState,
            snackBarState = snackBarState,
            onBackClick = {
                navigator.pop()
            },
            onUpdateClick = viewModel::onUpdateClick,
            onCheckUpdateClick = viewModel::onCheckForUpdateClick,
        )
        ConsumeSnackbarFlow(snackBarState, viewModel.snackBarMessage)
        var showUpdateDialog by rememberSaveable { mutableStateOf(false) }
        LaunchedEffect(uiState) {
            showUpdateDialog = uiState.newReleaseInfo != null
        }
        if (showUpdateDialog && uiState.newReleaseInfo != null) {
            AppUpdateDialog(
                appReleaseInfo = uiState.newReleaseInfo!!,
                onCancel = {
                    showUpdateDialog = false
                    viewModel.onCancelClick()
                },
                onUpdateClick = {
                    showUpdateDialog = false
                    viewModel.onUpdateClick()
                },
            )
        }
    }

    @Composable
    private fun AboutScreenContent(
        uiState: AboutUiState,
        snackBarState: SnackbarHostState,
        onBackClick: () -> Unit,
        onUpdateClick: () -> Unit,
        onCheckUpdateClick: () -> Unit,
    ) {
        val textHandler = LocalTextHandler.current
        val browserLauncher = LocalActivityBrowserLauncher.current
        val coroutineScope = rememberCoroutineScope()
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(LocalizedString.profileSettingAboutTitle),
                    onBackClick = onBackClick,
                )
            },
            snackbarHost = {
                SnackbarHost(snackBarState)
            },
        ) { innerPadding ->
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(88.dp),
                        painter = painterResource(com.zhangke.fread.commonbiz.Res.drawable.ic_fread_logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        text = AppCommonConfig.APP_NAME,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        text = textHandler.packageName,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    AboutClickableItem(
                        title = stringResource(LocalizedString.profileAboutWebsite),
                        clickableText = AppCommonConfig.WEBSITE,
                        showUnderline = true,
                        onClick = {
                            coroutineScope.launch {
                                browserLauncher.launchFreadLandingPage()
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val version = remember {
                        val versionName = textHandler.versionName
                        val versionCode = textHandler.versionCode
                        "$versionName($versionCode)"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().noRippleClick { onUpdateClick() },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AboutClickableItem(
                            modifier = Modifier,
                            title = stringResource(LocalizedString.profileAboutVersion),
                            clickableText = version,
                            showUnderline = false,
                            onClick = {},
                        )
                        if (uiState.newReleaseInfo != null) {
                            Text(
                                modifier = Modifier.padding(start = 6.dp),
                                text = stringResource(LocalizedString.profileSettingHaveNewVersion),
                                style = MaterialTheme.typography.labelMedium,
                                color = LocalContentColor.current.copy(alpha = 0.7F),
                                maxLines = 1,
                            )
                            Box(
                                modifier = Modifier.size(4.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = 0.8F)),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AboutClickableItem(
                        title = stringResource(LocalizedString.profileAboutDeveloper),
                        clickableText = AppCommonConfig.AUTHOR,
                        showUnderline = true,
                        onClick = {
                            coroutineScope.launch {
                                browserLauncher.launchAuthorWebsite()
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AboutClickableItem(
                        title = stringResource(LocalizedString.profileAboutContractUs),
                        clickableText = AppCommonConfig.AUTHOR_EMAIL,
                        showUnderline = false,
                        onClick = {
                            textHandler.copyText(AppCommonConfig.AUTHOR_EMAIL)
                            toast("Copied to clipboard")
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AboutClickableItem(
                        title = stringResource(LocalizedString.profileAboutTelegram),
                        clickableText = AppCommonConfig.TELEGRAM_GROUP,
                        showUnderline = false,
                        onClick = {
                            textHandler.copyText(AppCommonConfig.TELEGRAM_GROUP)
                            browserLauncher.launchBySystemBrowser(AppCommonConfig.TELEGRAM_GROUP)
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AboutClickableItem(
                        title = stringResource(LocalizedString.profileAboutPrivacyPolicy),
                        clickableText = AppCommonConfig.PRIVACY_POLICY,
                        showUnderline = false,
                        onClick = {
                            coroutineScope.launch {
                                browserLauncher.launchWebTabInApp(AppCommonConfig.PRIVACY_POLICY)
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(horizontal = 42.dp),
                        onClick = onCheckUpdateClick,
                    ) {
                        if (uiState.checkingUpdate) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                            )
                        } else {
                            Text(
                                text = stringResource(LocalizedString.profileSettingCheckForUpdate),
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AboutClickableItem(
        modifier: Modifier = Modifier,
        title: String,
        clickableText: String,
        showUnderline: Boolean,
        onClick: () -> Unit,
    ) {
        Row(
            modifier = modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .let {
                        if (showUnderline) {
                            it
                        } else {
                            it.noRippleClick {
                                onClick()
                            }
                        }
                    },
                text = title,
            )
            Text(
                modifier = Modifier.noRippleClick {
                    onClick()
                },
                text = clickableText,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = if (showUnderline) TextDecoration.Underline else null,
            )
        }
    }
}
