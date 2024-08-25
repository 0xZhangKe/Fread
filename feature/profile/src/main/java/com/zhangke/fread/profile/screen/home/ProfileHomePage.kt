package com.zhangke.fread.profile.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.size
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.fread.analytics.ProfileElements
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.login.LoginBottomSheetScreen
import com.zhangke.fread.profile.R
import com.zhangke.fread.profile.screen.setting.SettingScreen
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.richtext.FreadRichText

class ProfileHomePage : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel = getViewModel<ProfileHomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val rootNavigator = LocalNavigator.currentOrThrow.rootNavigator
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
                    reportClick(ProfileElements.ADD_ACCOUNT)
                    bottomSheetNavigator.show(LoginBottomSheetScreen())
                },
                onSettingClick = {
                    reportClick(ProfileElements.SETTING)
                    navigator.push(SettingScreen())
                },
                onLogoutClick = {
                    reportClick(ProfileElements.LOGOUT)
                    viewModel.onLogoutClick(it)
                },
                onAccountClick = {
                    reportClick(ProfileElements.ACCOUNT)
                    viewModel.onAccountClick(it)
                },
                onFavouritedClick = {
                    reportClick(ProfileElements.FAVOURITED)
                    viewModel.onFavouritedClick(it)
                },
                onBookmarkedClick = {
                    reportClick(ProfileElements.BOOKMARKED)
                    viewModel.onBookmarkedClick(it)
                },
                onFollowedHashtagClick = {
                    reportClick(ProfileElements.HASHTAG)
                    viewModel.onFollowedHashtagClick(it)
                },
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
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 32.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.profile_page_title),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.weight(1F))
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    items(uiState.accountDataList) { item ->
                        AccountGroupItem(
                            platform = item.first,
                            accountList = item.second,
                            onLogoutClick = onLogoutClick,
                            onAccountClick = onAccountClick,
                            onFavouritedClick = onFavouritedClick,
                            onBookmarkedClick = onBookmarkedClick,
                            onFollowedHashtagClick = onFollowedHashtagClick,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AccountGroupItem(
        platform: BlogPlatform,
        accountList: List<LoggedAccount>,
        onLogoutClick: (LoggedAccount) -> Unit,
        onAccountClick: (LoggedAccount) -> Unit,
        onFavouritedClick: (LoggedAccount) -> Unit,
        onBookmarkedClick: (LoggedAccount) -> Unit,
        onFollowedHashtagClick: (LoggedAccount) -> Unit,
    ) {
        Card(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                )
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = platform.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                accountList.forEach { account ->
                    LoggedAccountSection(
                        account = account,
                        onLogoutClick = {
                            onLogoutClick(account)
                        },
                        onAccountClick = onAccountClick,
                        onFavouritedClick = onFavouritedClick,
                        onBookmarkedClick = onBookmarkedClick,
                        onFollowedHashtagClick = onFollowedHashtagClick,
                    )
                }
            }
        }
    }

    @Composable
    private fun LoggedAccountSection(
        account: LoggedAccount,
        onLogoutClick: () -> Unit,
        onAccountClick: (LoggedAccount) -> Unit,
        onFavouritedClick: (LoggedAccount) -> Unit,
        onBookmarkedClick: (LoggedAccount) -> Unit,
        onFollowedHashtagClick: (LoggedAccount) -> Unit,
    ) {
        val context = LocalContext.current
        var showLogoutDialog by remember {
            mutableStateOf(false)
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .noRippleClick { onAccountClick(account) }) {
            BlogAuthorAvatar(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                imageUrl = account.avatar,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                FreadRichText(
                    modifier = Modifier
                        .padding(start = 16.dp),
                    maxLines = 1,
                    content = account.userName,
                    emojis = account.emojis,
                    fontSizeSp = 22F,
                    onUrlClick = {
                        BrowserLauncher.launchWebTabInApp(context, it, account.role)
                    },
                )
                FreadRichText(
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                    maxLines = 3,
                    content = account.description.orEmpty(),
                    emojis = account.emojis,
                    fontSizeSp = 16F,
                    onUrlClick = {
                        BrowserLauncher.launchWebTabInApp(context, it, account.role)
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SimpleIconButton(
                        iconModifier = Modifier.size(20.dp),
                        onClick = { onFavouritedClick(account) },
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    SimpleIconButton(
                        iconModifier = Modifier.size(20.dp),
                        onClick = { onBookmarkedClick(account) },
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmarks",
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    SimpleIconButton(
                        iconModifier = Modifier.size(20.dp),
                        onClick = {
                            onFollowedHashtagClick(account)
                        },
                        imageVector = Icons.Default.Tag,
                        contentDescription = "Followed Tags",
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    SimpleIconButton(
                        iconModifier = Modifier.size(20.dp),
                        onClick = { showLogoutDialog = true },
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Followed Tags",
                    )
                }
            }
        }
        if (showLogoutDialog) {
            FreadDialog(
                onDismissRequest = { showLogoutDialog = false },
                contentText = stringResource(id = R.string.profile_page_logout_dialog_content),
                onPositiveClick = {
                    showLogoutDialog = false
                    onLogoutClick()
                },
                onNegativeClick = {
                    showLogoutDialog = false
                },
            )
        }
    }

    private val LoggedAccount.role: IdentityRole
        get() = IdentityRole(accountUri = uri, baseUrl = platform.baseUrl)
}
