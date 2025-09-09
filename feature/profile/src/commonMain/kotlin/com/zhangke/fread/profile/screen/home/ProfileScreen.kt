package com.zhangke.fread.profile.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.LocalModuleScreenVisitor
import com.zhangke.fread.commonbiz.shared.composable.UserInfoCard
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.profile.screen.setting.SettingScreen
import com.zhangke.fread.status.account.LoggedAccount
import org.jetbrains.compose.resources.stringResource

class ProfileScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        CompositionLocalProvider(LocalNavigator provides navigator) {
            val viewModel = getViewModel<ProfileHomeViewModel>()
            val uiState by viewModel.uiState.collectAsState()
            val moduleScreenVisitor = LocalModuleScreenVisitor.current
            LaunchedEffect(Unit) {
                viewModel.refreshAccountInfo()
            }
            ProfileHomePageContent(
                uiState = uiState,
                onAddAccountClick = {
                    navigator.push(moduleScreenVisitor.feedsScreenVisitor.getAddContentScreen())
                },
                onSettingClick = {
                    navigator.push(SettingScreen())
                },
                onAccountClick = {
                    viewModel.onAccountClick(it)
                },
                onLoginClick = viewModel::onLoginClick,
            )
            ConsumeFlow(viewModel.openPageFlow) {
                navigator.push(it)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ProfileHomePageContent(
        uiState: ProfileHomeUiState,
        onAddAccountClick: () -> Unit,
        onSettingClick: () -> Unit,
        onAccountClick: (LoggedAccount) -> Unit,
        onLoginClick: (LoggedAccount) -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier,
                            text = stringResource(LocalizedString.profilePageTitle),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    actions = {
                        SimpleIconButton(
                            onClick = onAddAccountClick,
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Account",
                        )
                        SimpleIconButton(
                            onClick = onSettingClick,
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                        )
                    },
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 66.dp),
                ) {
                    items(uiState.accountDataList) { item ->
                        AccountDetail(
                            modifier = Modifier.fillMaxWidth(),
                            accountDetail = item,
                            onAccountClick = onAccountClick,
                            onLoginClick = onLoginClick,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun AccountDetail(
        modifier: Modifier,
        accountDetail: ProfileAccountUiState,
        onAccountClick: (LoggedAccount) -> Unit,
        onLoginClick: (LoggedAccount) -> Unit,
    ) {
        UserInfoCard(
            modifier = modifier.padding(horizontal = 16.dp),
            user = accountDetail.account.author,
            showActiveState = accountDetail.active,
            actionButton = if (accountDetail.authFailed) {
                {
                    TextButton(
                        modifier = Modifier,
                        onClick = { onLoginClick(accountDetail.account.account) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        Text(text = stringResource(LocalizedString.profileAccountNotLogin))
                    }
                }
            } else {
                null
            },
            onUserClick = { onAccountClick(accountDetail.account.account) },
        )
    }
}
