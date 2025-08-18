package com.zhangke.fread.profile.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.fread.commonbiz.shared.LocalModuleScreenVisitor
import com.zhangke.fread.commonbiz.shared.composable.UserInfoCard
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.ic_profile_tab
import com.zhangke.fread.feature.profile.profile_account_not_login
import com.zhangke.fread.feature.profile.profile_page_title
import com.zhangke.fread.profile.screen.setting.SettingScreen
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.isBluesky
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class ProfileTab() : PagerTab {

    override val options: PagerTabOptions
        @Composable get() {
            val icon = painterResource(Res.drawable.ic_profile_tab)
            return remember {
                PagerTabOptions(
                    title = "Profile", icon = icon
                )
            }
        }

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?,
    ) {
        val viewModel = screen.getViewModel<ProfileHomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val moduleScreenVisitor = LocalModuleScreenVisitor.current
        LaunchedEffect(Unit) {
            viewModel.refreshAccountInfo()
        }
        val navigator = LocalNavigator.currentOrThrow
        ProfileHomePageContent(
            uiState = uiState,
            onAddAccountClick = {
                navigator.push(moduleScreenVisitor.feedsScreenVisitor.getAddContentScreen())
            },
            onSettingClick = {
                navigator.push(SettingScreen())
            },
            onLogoutClick = {
                viewModel.onLogoutClick(it)
            },
            onAccountClick = {
                viewModel.onAccountClick(it)
            },
            onFavouritedClick = {
                viewModel.onFavouritedClick(it)
            },
            onBookmarkedClick = {
                viewModel.onBookmarkedClick(it)
            },
            onFollowedHashtagClick = {
                viewModel.onFollowedHashtagClick(it)
            },
            onPinnedFeedsClick = {
                viewModel.onPinnedFeedsClick(it)
            },
            onLoginClick = viewModel::onLoginClick,
            onListsClick = viewModel::onListsClick,
        )
        ConsumeFlow(viewModel.openPageFlow) {
            navigator.push(it)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ProfileHomePageContent(
        uiState: ProfileHomeUiState,
        onAddAccountClick: () -> Unit,
        onSettingClick: () -> Unit,
        onLogoutClick: (LoggedAccount) -> Unit,
        onAccountClick: (LoggedAccount) -> Unit,
        onFavouritedClick: (LoggedAccount) -> Unit,
        onBookmarkedClick: (LoggedAccount) -> Unit,
        onFollowedHashtagClick: (LoggedAccount) -> Unit,
        onPinnedFeedsClick: (LoggedAccount) -> Unit,
        onLoginClick: (LoggedAccount) -> Unit,
        onListsClick: (LoggedAccount) -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier,
                            text = stringResource(Res.string.profile_page_title),
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
                            onLogoutClick = onLogoutClick,
                            onFavouritedClick = onFavouritedClick,
                            onBookmarkedClick = onBookmarkedClick,
                            onFollowedHashtagClick = onFollowedHashtagClick,
                            onPinnedFeedsClick = onPinnedFeedsClick,
                            onListsClick = onListsClick,
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
        onFavouritedClick: (LoggedAccount) -> Unit,
        onBookmarkedClick: (LoggedAccount) -> Unit,
        onFollowedHashtagClick: (LoggedAccount) -> Unit,
        onPinnedFeedsClick: (LoggedAccount) -> Unit,
        onListsClick: (LoggedAccount) -> Unit,
        onLogoutClick: (LoggedAccount) -> Unit,
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
                        Text(text = stringResource(Res.string.profile_account_not_login))
                    }
                }
            } else {
                null
            },
            onUserClick = { onAccountClick(accountDetail.account.account) },
            bottomPanel = {
                AccountInteractionPanel(
                    modifier = Modifier.fillMaxWidth(),
                    account = accountDetail.account.account,
                    onLikedClick = onFavouritedClick,
                    onBookmarkedClick = onBookmarkedClick,
                    onListsClick = onListsClick,
                    onLogoutClick = onLogoutClick,
                    onFollowedHashtagClick = onFollowedHashtagClick,
                    onPinnedFeedsClick = onPinnedFeedsClick,
                )
            },
        )
    }


    @Composable
    private fun AccountInteractionPanel(
        modifier: Modifier,
        account: LoggedAccount,
        onLikedClick: (LoggedAccount) -> Unit,
        onBookmarkedClick: (LoggedAccount) -> Unit,
        onListsClick: (LoggedAccount) -> Unit,
        onLogoutClick: (LoggedAccount) -> Unit,
        onFollowedHashtagClick: (LoggedAccount) -> Unit,
        onPinnedFeedsClick: (LoggedAccount) -> Unit,
    ) {
        val isBluesky = account.platform.protocol.isBluesky
        val iconSize = 20.dp
        Row(
            modifier = modifier.fillMaxWidth()
                .padding(end = 8.dp),
            verticalAlignment = Alignment.Companion.CenterVertically,
        ) {
            SimpleIconButton(
                iconModifier = Modifier.Companion.size(iconSize),
                onClick = { onLikedClick(account) },
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "Liked",
            )
            if (!isBluesky) {
                Spacer(modifier = Modifier.Companion.weight(1F))
                SimpleIconButton(
                    iconModifier = Modifier.Companion.size(iconSize),
                    onClick = { onBookmarkedClick(account) },
                    imageVector = Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmarks",
                )
                Spacer(modifier = Modifier.Companion.weight(1F))
                SimpleIconButton(
                    iconModifier = Modifier.Companion.size(iconSize),
                    onClick = { onListsClick(account) },
                    imageVector = Icons.AutoMirrored.Outlined.ListAlt,
                    contentDescription = "Lists",
                )
            }
            Spacer(modifier = Modifier.Companion.weight(1F))
            SimpleIconButton(
                iconModifier = Modifier.Companion.size(iconSize),
                onClick = {
                    if (isBluesky) {
                        onPinnedFeedsClick(account)
                    } else {
                        onFollowedHashtagClick(account)
                    }
                },
                imageVector = Icons.Default.Tag,
                contentDescription = "Tags",
            )
        }
    }
}