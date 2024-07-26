package com.zhangke.fread.profile.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.SimpleIconButton
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
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
                    )
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
    ) {
        Card(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = platform.name,
                    style = MaterialTheme.typography.titleLarge,
                )
                accountList.forEach { account ->
                    LoggedAccountSection(
                        account = account,
                        onLogoutClick = {
                            onLogoutClick(account)
                        },
                        onAccountClick = onAccountClick,
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
    ) {
        val context = LocalContext.current
        ConstraintLayout(
            modifier = Modifier
                .clickable { onAccountClick(account) }
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            val (avatar, content, moreIcon) = createRefs()
            AsyncImage(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .constrainAs(avatar) {
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(content.start)
                        top.linkTo(parent.top, 12.dp)
                    },
                placeholder = painterResource(id = com.zhangke.fread.framework.R.drawable.ic_avatar),
                error = painterResource(id = com.zhangke.fread.framework.R.drawable.ic_avatar),
                model = account.avatar,
                contentDescription = "Avatar",
            )
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(content) {
                        start.linkTo(avatar.end, 16.dp)
                        top.linkTo(parent.top, 8.dp)
                        end.linkTo(moreIcon.start)
                        bottom.linkTo(parent.bottom, 12.dp)
                        width = Dimension.fillToConstraints
                    },
                horizontalAlignment = Alignment.Start,
            ) {
                FreadRichText(
                    modifier = Modifier,
                    maxLines = 1,
                    content = account.userName,
                    emojis = account.emojis,
                    fontSizeSp = 22F,
                    onUrlClick = {
                        BrowserLauncher.launchWebTabInApp(context, it, account.role)
                    },
                )
                FreadRichText(
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 3,
                    content = account.description.orEmpty(),
                    emojis = account.emojis,
                    onUrlClick = {
                        BrowserLauncher.launchWebTabInApp(context, it, account.role)
                    }
                )
            }
            var showMorePopup by remember {
                mutableStateOf(false)
            }
            Box(
                modifier = Modifier.constrainAs(moreIcon) {
                    start.linkTo(content.end, 16.dp)
                    end.linkTo(parent.end, 24.dp)
                    top.linkTo(parent.top, 12.dp)
                    bottom.linkTo(parent.bottom)
                }) {
                IconButton(
                    onClick = { showMorePopup = true },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More option",
                    )
                }
                DropdownMenu(
                    expanded = showMorePopup,
                    onDismissRequest = { showMorePopup = false },
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(com.zhangke.fread.commonbiz.R.string.logout))
                        },
                        onClick = {
                            onLogoutClick()
                            showMorePopup = false
                        },
                    )
                }
            }
        }
    }

    private val LoggedAccount.role: IdentityRole
        get() = IdentityRole(accountUri = uri, baseUrl = platform.baseUrl)
}
