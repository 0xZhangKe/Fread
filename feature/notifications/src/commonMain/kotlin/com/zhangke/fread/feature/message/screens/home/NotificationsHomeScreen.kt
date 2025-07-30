package com.zhangke.fread.feature.message.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.AnimatedScreenContentScope
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.fread.common.page.BaseAnimatedScreen
import com.zhangke.fread.commonbiz.illustration_message
import com.zhangke.fread.feature.notifications.Res
import com.zhangke.fread.feature.notifications.notification_tab_title
import com.zhangke.fread.feature.notifications.notifications_account_empty_tip
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.common.SelectAccountDialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class NotificationsHomeScreen : BaseAnimatedScreen() {

    @Composable
    override fun AnimationContent(animatedScreenContentScope: AnimatedScreenContentScope) {
        super.AnimationContent(animatedScreenContentScope)
        val viewModel: NotificationsHomeViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        CompositionLocalProvider(
            LocalNavigator provides navigator
        ) {
            NotificationsHomeScreenContent(
                uiState = uiState,
                onAccountSelected = viewModel::onAccountSelected,
                animatedScreenContentScope = animatedScreenContentScope,
            )
        }
    }

    @Composable
    private fun NotificationsHomeScreenContent(
        uiState: NotificationsHomeUiState,
        onAccountSelected: (LoggedAccount) -> Unit,
        animatedScreenContentScope: AnimatedScreenContentScope,
    ) {
        val snackbarHost = rememberSnackbarHostState()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (uiState.tabs.size > 1 && uiState.selectedAccount != null) {
                    NotificationTopBar(
                        account = uiState.selectedAccount,
                        accountList = uiState.accountList,
                        onAccountSelected = onAccountSelected,
                    )
                } else {
                    Spacer(
                        modifier = Modifier
                            .statusBarsPadding()
                            .height(16.dp)
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHost,
                    modifier = Modifier.padding(bottom = 60.dp),
                )
            },
        ) { paddings ->
            if (uiState.tabs.isEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(paddings)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentScale = ContentScale.Inside,
                        painter = painterResource(com.zhangke.fread.commonbiz.Res.drawable.illustration_message),
                        contentDescription = null,
                    )

                    Text(
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        text = stringResource(Res.string.notifications_account_empty_tip),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(paddings)
                        .fillMaxSize(),
                ) {
                    val pagerState = rememberPagerState {
                        uiState.tabs.size
                    }
                    LaunchedEffect(uiState) {
                        val index = uiState.accountList.indexOf(uiState.selectedAccount)
                        if (index >= 0) {
                            pagerState.scrollToPage(index)
                        }
                    }
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides snackbarHost,
                    ) {
                        HorizontalPager(
                            modifier = Modifier.fillMaxSize(),
                            state = pagerState,
                            userScrollEnabled = false,
                        ) { pageIndex ->
                            with(uiState.tabs[pageIndex]) {
                                TabContent(
                                    this@NotificationsHomeScreen,
                                    null,
                                    animatedScreenContentScope
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NotificationTopBar(
        account: LoggedAccount,
        accountList: List<LoggedAccount>,
        onAccountSelected: (LoggedAccount) -> Unit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(Res.string.notification_tab_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.weight(1F))
            Spacer(modifier = Modifier.width(8.dp))
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
        }
    }
}
