package com.zhangke.fread.profile.screen.setting.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.toast.toast
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.common.handler.LocalActivityTextHandler
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.ic_fread_logo
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.profile_about_contract_us
import com.zhangke.fread.feature.profile.profile_about_developer
import com.zhangke.fread.feature.profile.profile_about_privacy_policy
import com.zhangke.fread.feature.profile.profile_about_telegram
import com.zhangke.fread.feature.profile.profile_about_version
import com.zhangke.fread.feature.profile.profile_about_website
import com.zhangke.fread.feature.profile.profile_setting_about_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class AboutScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        AboutScreenContent(
            onBackClick = {
                navigator.pop()
            },
        )
    }

    @Composable
    private fun AboutScreenContent(
        onBackClick: () -> Unit,
    ) {
        val textHandler = LocalActivityTextHandler.current
        val browserLauncher = LocalActivityBrowserLauncher.current
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.profile_setting_about_title),
                    onBackClick = onBackClick,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 32.dp)
                        .size(80.dp),
                    painter = painterResource(com.zhangke.fread.commonbiz.Res.drawable.ic_fread_logo),
                    contentDescription = "Logo",
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
                    title = stringResource(Res.string.profile_about_website),
                    clickableText = AppCommonConfig.WEBSITE,
                    showUnderline = true,
                    onClick = {
                        browserLauncher.launchFreadLandingPage()
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                val version = remember {
                    val versionName = textHandler.versionName
                    val versionCode = textHandler.versionCode
                    "$versionName($versionCode)"
                }
                AboutClickableItem(
                    title = stringResource(Res.string.profile_about_version),
                    clickableText = version,
                    showUnderline = false,
                    onClick = {},
                )
                Spacer(modifier = Modifier.height(16.dp))
                AboutClickableItem(
                    title = stringResource(Res.string.profile_about_developer),
                    clickableText = AppCommonConfig.AUTHOR,
                    showUnderline = true,
                    onClick = {
                        browserLauncher.launchAuthorWebsite()
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                AboutClickableItem(
                    title = stringResource(Res.string.profile_about_contract_us),
                    clickableText = AppCommonConfig.AUTHOR_EMAIL,
                    showUnderline = false,
                    onClick = {
                        textHandler.copyText(AppCommonConfig.AUTHOR_EMAIL)
                        toast("Copied to clipboard")
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                AboutClickableItem(
                    title = stringResource(Res.string.profile_about_telegram),
                    clickableText = AppCommonConfig.TELEGRAM_GROUP,
                    showUnderline = false,
                    onClick = {
                        textHandler.copyText(AppCommonConfig.TELEGRAM_GROUP)
                        browserLauncher.launchBySystemBrowser(AppCommonConfig.TELEGRAM_GROUP)
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                AboutClickableItem(
                    title = stringResource(Res.string.profile_about_privacy_policy),
                    clickableText = AppCommonConfig.PRIVACY_POLICY,
                    showUnderline = false,
                    onClick = {
                        browserLauncher.launchWebTabInApp(AppCommonConfig.PRIVACY_POLICY)
                    },
                )
            }
        }
    }

    @Composable
    private fun AboutClickableItem(
        title: String,
        clickableText: String,
        showUnderline: Boolean,
        onClick: () -> Unit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .let {
                        if (showUnderline) {
                            it
                        } else {
                            it.clickable {
                                onClick()
                            }
                        }
                    },
                text = title,
            )
            Text(
                modifier = Modifier.clickable {
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
