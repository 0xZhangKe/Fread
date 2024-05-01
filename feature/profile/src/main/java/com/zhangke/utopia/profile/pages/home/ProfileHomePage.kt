package com.zhangke.utopia.profile.pages.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.utopia.commonbiz.shared.screen.login.LoginBottomSheetScreen
import com.zhangke.utopia.profile.R
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.ui.richtext.UtopiaRichText

class ProfileHomePage : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel = getViewModel<ProfileHomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        ProfileHomePageContent(
            uiState = uiState,
            onAddAccountClick = {
                bottomSheetNavigator.show(LoginBottomSheetScreen())
            },
            onLogoutClick = viewModel::onLogoutClick,
            onAccountClick = viewModel::onAccountClick,
        )
        ConsumeFlow(viewModel.openPageFlow) {
            navigator.push(it)
        }
    }

    @Composable
    private fun ProfileHomePageContent(
        uiState: ProfileHomeUiState,
        onAddAccountClick: () -> Unit,
        onLogoutClick: (LoggedAccount) -> Unit,
        onAccountClick: (LoggedAccount) -> Unit,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 56.dp),
                text = stringResource(R.string.profile_page_title),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp),
                text = stringResource(R.string.profile_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

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
            Button(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = onAddAccountClick,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Account"
                )
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
                    color = MaterialTheme.colorScheme.onSurface,
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
        ConstraintLayout(
            modifier = Modifier
                .clickable { onAccountClick(account) }
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            val (avatar, content, moreIcon) = createRefs()
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .constrainAs(avatar) {
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(content.start)
                        top.linkTo(parent.top, 12.dp)
                    },
                placeholder = painterResource(id = com.zhangke.utopia.framework.R.drawable.ic_avatar),
                error = painterResource(id = com.zhangke.utopia.framework.R.drawable.ic_avatar),
                model = account.avatar,
                contentDescription = "Avatar",
            )
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(content) {
                        start.linkTo(avatar.end, 16.dp)
                        top.linkTo(parent.top, 12.dp)
                        end.linkTo(moreIcon.start)
                        bottom.linkTo(parent.bottom, 12.dp)
                        width = Dimension.fillToConstraints
                    },
                horizontalAlignment = Alignment.Start,
            ) {
                Row {
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        maxLines = 1,
                        text = account.userName,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                if (account.description.isNullOrEmpty().not()) {
                    UtopiaRichText(
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 3,
                        content = account.description!!,
                        mentions = emptyList(),
                    )
                }
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
                            Text(text = stringResource(com.zhangke.utopia.commonbiz.R.string.logout))
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
}
