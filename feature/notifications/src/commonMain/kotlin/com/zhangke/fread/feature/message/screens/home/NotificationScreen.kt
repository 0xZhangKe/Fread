package com.zhangke.fread.feature.message.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.blur.applyBlurEffect
import com.zhangke.framework.blur.blurEffectContainerColor
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.SingleRowTopAppBar
import com.zhangke.framework.composable.TopAppBarColors
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.updateTopPadding
import com.zhangke.framework.utils.pxToDp
import com.zhangke.fread.commonbiz.illustration_message
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.common.SelectAccountDialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationScreen() {
    val viewModel: NotificationsHomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    NotificationsHomeScreenContent(
        uiState = uiState,
        onAccountSelected = viewModel::onAccountSelected,
    )
}

@Composable
private fun NotificationsHomeScreenContent(
    uiState: NotificationsHomeUiState,
    onAccountSelected: (LoggedAccount) -> Unit,
) {
    val density = LocalDensity.current
    val snackbarHost = rememberSnackbarHostState()
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        var topBarHeight: Dp by remember { mutableStateOf(0.dp) }
        if (uiState.tabs.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.fillMaxWidth().height(topBarHeight))

                Image(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentScale = ContentScale.Inside,
                    painter = painterResource(com.zhangke.fread.commonbiz.Res.drawable.illustration_message),
                    contentDescription = null,
                )

                Text(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp
                    ),
                    text = stringResource(LocalizedString.notificationsAccountEmptyTip),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                val pagerState = rememberPagerState { uiState.tabs.size }
                LaunchedEffect(uiState) {
                    val index = uiState.accountList.indexOf(uiState.selectedAccount)
                    if (index >= 0) {
                        pagerState.scrollToPage(index)
                    }
                }
                CompositionLocalProvider(
                    LocalSnackbarHostState provides snackbarHost,
                    LocalContentPadding provides updateTopPadding(topBarHeight),
                ) {
                    HorizontalPager(
                        modifier = Modifier.fillMaxSize(),
                        state = pagerState,
                        userScrollEnabled = false,
                    ) { pageIndex ->
                        with(uiState.tabs[pageIndex]) {
                            Content()
                        }
                    }
                }
            }
        }
        if (uiState.tabs.size > 1 && uiState.selectedAccount != null) {
            NotificationTopBar(
                account = uiState.selectedAccount,
                accountList = uiState.accountList,
                onAccountSelected = onAccountSelected,
                onHeightChanged = { topBarHeight = it },
            )
        } else {
            Spacer(
                modifier = Modifier.statusBarsPadding()
                    .height(16.dp)
                    .onSizeChanged { topBarHeight = it.height.pxToDp(density) }
            )
        }
        SnackbarHost(
            hostState = snackbarHost,
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = LocalContentPadding.current.calculateBottomPadding() + 16.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationTopBar(
    account: LoggedAccount,
    accountList: List<LoggedAccount>,
    onAccountSelected: (LoggedAccount) -> Unit,
    onHeightChanged: (Dp) -> Unit,
) {
    val density = LocalDensity.current
    val containerColor = MaterialTheme.colorScheme.surface
    SingleRowTopAppBar(
        modifier = Modifier.applyBlurEffect(containerColor = containerColor)
            .onSizeChanged { onHeightChanged(it.height.pxToDp(density)) },
        title = {
            Text(
                text = stringResource(LocalizedString.notificationTabTitle),
            )
        },
        colors = TopAppBarColors.default(
            containerColor = blurEffectContainerColor(true, containerColor),
        ),
        actions = {
            var showSelectAccountPopup by remember {
                mutableStateOf(false)
            }
            Box(modifier = Modifier.padding(end = 8.dp)) {
                Row(
                    modifier = Modifier.noRippleClick {
                        showSelectAccountPopup = !showSelectAccountPopup
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BlogAuthorAvatar(
                        modifier = Modifier.size(32.dp),
                        imageUrl = account.avatar,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = account.userName,
                            style = MaterialTheme.typography.titleMedium
                                .copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = account.prettyHandle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Account",
                    )
                }
                if (showSelectAccountPopup) {
                    SelectAccountDialog(
                        accountList = accountList,
                        selectedAccounts = listOf(account),
                        onDismissRequest = { showSelectAccountPopup = false },
                        onAccountClicked = onAccountSelected,
                    )
                }
            }
        },
    )
}
