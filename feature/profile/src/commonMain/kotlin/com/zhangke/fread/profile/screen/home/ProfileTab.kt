package com.zhangke.fread.profile.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.text.font.FontWeight
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
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.voyager.AnimatedScreenContentScope
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.resources.PlatformLogo
import com.zhangke.fread.commonbiz.shared.LocalModuleScreenVisitor
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.ic_profile_tab
import com.zhangke.fread.feature.profile.profile_account_not_login
import com.zhangke.fread.feature.profile.profile_page_logout_dialog_content
import com.zhangke.fread.feature.profile.profile_page_title
import com.zhangke.fread.profile.screen.setting.SettingScreen
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.isBluesky
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.richtext.FreadRichText
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
        animatedScreenContentScope: AnimatedScreenContentScope?
    ) {
        val viewModel = screen.getViewModel<ProfileHomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val rootNavigator = LocalNavigator.currentOrThrow.rootNavigator
        val moduleScreenVisitor = LocalModuleScreenVisitor.current
        LaunchedEffect(Unit) {
            viewModel.refreshAccountInfo()
        }
        CompositionLocalProvider(
            LocalNavigator provides rootNavigator
        ) {
            val navigator = LocalNavigator.currentOrThrow
            ProfileHomePageContent(
                uiState = uiState,
                onAddAccountClick = {
                    rootNavigator.push(moduleScreenVisitor.feedsScreenVisitor.getAddContentScreen())
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
    }

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
        Surface(
            modifier = Modifier.Companion
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Column {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 32.dp, end = 16.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.Companion,
                        text = stringResource(Res.string.profile_page_title),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.Companion.weight(1F))
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
                }

                LazyColumn(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentPadding = PaddingValues(bottom = 60.dp)
                ) {
                    items(uiState.accountDataList) { item ->
                        AccountGroupItem(
                            accountList = item.second,
                            onLogoutClick = onLogoutClick,
                            onAccountClick = onAccountClick,
                            onFavouritedClick = onFavouritedClick,
                            onBookmarkedClick = onBookmarkedClick,
                            onFollowedHashtagClick = onFollowedHashtagClick,
                            onPinnedFeedsClick = onPinnedFeedsClick,
                            onLoginClick = onLoginClick,
                            onListsClick = onListsClick,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AccountGroupItem(
        accountList: List<ProfileAccountUiState>,
        onLogoutClick: (LoggedAccount) -> Unit,
        onAccountClick: (LoggedAccount) -> Unit,
        onFavouritedClick: (LoggedAccount) -> Unit,
        onBookmarkedClick: (LoggedAccount) -> Unit,
        onFollowedHashtagClick: (LoggedAccount) -> Unit,
        onPinnedFeedsClick: (LoggedAccount) -> Unit,
        onLoginClick: (LoggedAccount) -> Unit,
        onListsClick: (LoggedAccount) -> Unit,
    ) {
        Card(
            modifier = Modifier.Companion
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.Companion
                    .padding(start = 16.dp, top = 16.dp)
            ) {
                accountList.forEach { account ->
                    LoggedAccountSection(
                        accountUiState = account,
                        onLogoutClick = onLogoutClick,
                        onAccountClick = onAccountClick,
                        onFavouritedClick = onFavouritedClick,
                        onBookmarkedClick = onBookmarkedClick,
                        onFollowedHashtagClick = onFollowedHashtagClick,
                        onPinnedFeedsClick = onPinnedFeedsClick,
                        onLoginClick = onLoginClick,
                        onListsClick = onListsClick,
                    )
                }
            }
        }
    }

    @Composable
    private fun LoggedAccountSection(
        accountUiState: ProfileAccountUiState,
        onLogoutClick: (LoggedAccount) -> Unit,
        onAccountClick: (LoggedAccount) -> Unit,
        onFavouritedClick: (LoggedAccount) -> Unit,
        onBookmarkedClick: (LoggedAccount) -> Unit,
        onFollowedHashtagClick: (LoggedAccount) -> Unit,
        onPinnedFeedsClick: (LoggedAccount) -> Unit,
        onLoginClick: (LoggedAccount) -> Unit,
        onListsClick: (LoggedAccount) -> Unit,
    ) {
        val account = accountUiState.account
        val browserLauncher = LocalActivityBrowserLauncher.current
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .noRippleClick { onAccountClick(account) }) {
            BlogAuthorAvatar(
                modifier = Modifier.Companion
                    .size(48.dp)
                    .clip(CircleShape),
                imageUrl = account.avatar,
            )
            Column(
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    verticalAlignment = Alignment.Companion.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.Companion.weight(1F),
                    ) {
                        Row(
                            modifier = Modifier.Companion.fillMaxWidth(),
                            verticalAlignment = Alignment.Companion.CenterVertically,
                        ) {
                            FreadRichText(
                                modifier = Modifier.Companion.padding(start = 16.dp),
                                maxLines = 1,
                                content = account.userName,
                                emojis = account.emojis,
                                fontSizeSp = 18F,
                                fontWeight = FontWeight.Companion.SemiBold,
                                onUrlClick = {
                                    browserLauncher.launchWebTabInApp(it, account.locator)
                                },
                            )
                            Spacer(modifier = Modifier.Companion.width(4.dp))
                            PlatformLogo(
                                modifier = Modifier.Companion.size(14.dp),
                                protocol = account.platform.protocol,
                            )
                        }
                        Text(
                            modifier = Modifier.Companion.padding(start = 16.dp, top = 2.dp),
                            text = account.prettyHandle,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (!accountUiState.logged) {
                        TextButton(
                            modifier = Modifier.Companion.padding(end = 8.dp),
                            onClick = { onLoginClick(account) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                        ) {
                            Text(text = stringResource(Res.string.profile_account_not_login))
                        }
                    } else {
                        Spacer(modifier = Modifier.Companion.width(16.dp))
                    }
                }
                FreadRichText(
                    modifier = Modifier.Companion.padding(start = 16.dp, top = 4.dp),
                    maxLines = 5,
                    content = account.description.orEmpty(),
                    emojis = account.emojis,
                    fontSizeSp = 16F,
                    onUrlClick = {
                        browserLauncher.launchWebTabInApp(it, account.locator)
                    },
                )
                AccountInteractionPanel(
                    modifier = Modifier.Companion.padding(end = 8.dp),
                    account = account,
                    onLikedClick = onFavouritedClick,
                    onBookmarkedClick = onBookmarkedClick,
                    onLogoutClick = onLogoutClick,
                    onFollowedHashtagClick = onFollowedHashtagClick,
                    onPinnedFeedsClick = onPinnedFeedsClick,
                    onListsClick = onListsClick,
                )
            }
        }
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
        var showLogoutDialog by remember { mutableStateOf(false) }
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
            Spacer(modifier = Modifier.Companion.weight(1F))
            SimpleIconButton(
                iconModifier = Modifier.Companion.size(iconSize),
                onClick = { showLogoutDialog = true },
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
            )
        }
        if (showLogoutDialog) {
            FreadDialog(
                onDismissRequest = { showLogoutDialog = false },
                contentText = stringResource(Res.string.profile_page_logout_dialog_content),
                onPositiveClick = {
                    showLogoutDialog = false
                    onLogoutClick(account)
                },
                onNegativeClick = {
                    showLogoutDialog = false
                },
            )
        }
    }

    private val LoggedAccount.locator: PlatformLocator
        get() = PlatformLocator(accountUri = uri, baseUrl = platform.baseUrl)
}