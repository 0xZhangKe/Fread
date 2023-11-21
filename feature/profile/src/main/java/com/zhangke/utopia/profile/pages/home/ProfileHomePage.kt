package com.zhangke.utopia.profile.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.zhangke.framework.composable.size
import com.zhangke.utopia.profile.R
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.platform.BlogPlatform

@Composable
internal fun ProfileHomePage(
    uiState: ProfileHomeUiState,
    onAddAccountClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 56.dp),
            text = stringResource(R.string.profile_page_title),
            style = MaterialTheme.typography.displayMedium,
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
                )
            }
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            onClick = onAddAccountClick,
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
                    onActiveClicked = {},
                )
            }
        }
    }
}

@Composable
private fun LoggedAccountSection(
    account: LoggedAccount,
    onActiveClicked: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
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
                if (account.active) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 6.dp)
                            .clip(CircleShape)
                            .size(8.dp)
                            .background(Color(0xFF00BF00))
                    )
                }
            }
            if (account.description.isNullOrEmpty().not()) {
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 3,
                    text = account.description!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
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
                if (!account.active) {
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(com.zhangke.utopia.commonbiz.R.string.select))
                        },
                        onClick = { /*TODO*/ },
                    )
                }
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(com.zhangke.utopia.commonbiz.R.string.logout))
                    },
                    onClick = { /*TODO*/ },
                )
            }
        }
    }
}
