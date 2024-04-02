package com.zhangke.utopia.explore.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.utopia.explore.R
import com.zhangke.utopia.explore.screens.home.tab.ExplorerFeedsTab
import com.zhangke.utopia.explore.screens.home.tab.ExplorerFeedsTabType
import com.zhangke.utopia.explore.screens.search.bar.ExplorerSearchBar
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.ui.BlogAuthorAvatar

class ExplorerHomeScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = getViewModel<ExplorerHomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        ExplorerHomeContent(
            uiState = uiState,
            onAccountSelected = viewModel::onAccountSelected,
        )
    }

    @Composable
    private fun ExplorerHomeContent(
        uiState: ExplorerHomeUiState,
        onAccountSelected: (LoggedAccount) -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                val selectedAccount = uiState.selectedAccount
                if (uiState.loggedAccountsList.size < 2 || selectedAccount == null) {
                    ExplorerSearchBar()
                } else {
                    SelectAccountTopBar(
                        account = uiState.selectedAccount,
                        accountList = uiState.loggedAccountsList,
                        onAccountSelected = onAccountSelected,
                    )
                }
                if (selectedAccount != null) {
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides snackbarHostState
                    ) {
                        val tabs = remember(selectedAccount) {
                            listOf(
                                ExplorerFeedsTab(
                                    type = ExplorerFeedsTabType.STATUS,
                                    accountUri = selectedAccount.uri,
                                ),
                                ExplorerFeedsTab(
                                    type = ExplorerFeedsTabType.HASHTAG,
                                    accountUri = selectedAccount.uri,
                                ),
                                ExplorerFeedsTab(
                                    type = ExplorerFeedsTabType.USERS,
                                    accountUri = selectedAccount.uri,
                                ),
                            )
                        }
                        HorizontalPagerWithTab(
                            tabList = tabs,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SelectAccountTopBar(
        account: LoggedAccount,
        accountList: List<LoggedAccount>,
        onAccountSelected: (LoggedAccount) -> Unit,
    ) {
        var selectAccountPopupExpanded by remember {
            mutableStateOf(false)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.explorer_tab_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.weight(1F))
            SimpleIconButton(
                onClick = {},
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                Row(
                    modifier = Modifier.clickable {
                        selectAccountPopupExpanded = !selectAccountPopupExpanded
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BlogAuthorAvatar(
                        modifier = Modifier.size(32.dp),
                        imageUrl = account.avatar,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = account.userName,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    SimpleIconButton(
                        onClick = {
                            selectAccountPopupExpanded = !selectAccountPopupExpanded
                        },
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Account",
                    )
                }
                DropdownMenu(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    expanded = selectAccountPopupExpanded,
                    onDismissRequest = { selectAccountPopupExpanded = false },
                ) {
                    accountList.forEach {
                        DropdownMenuItem(
                            text = {
                                Row {
                                    Text(
                                        modifier = Modifier.alignByBaseline(),
                                        text = it.userName,
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 2.dp)
                                            .alignByBaseline(),
                                        text = "@${it.webFinger.host}",
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                            },
                            onClick = {
                                selectAccountPopupExpanded = false
                                onAccountSelected(it)
                            },
                        )
                    }
                }
            }
        }
    }
}
