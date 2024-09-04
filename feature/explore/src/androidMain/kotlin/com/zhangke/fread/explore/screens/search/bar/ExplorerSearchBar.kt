package com.zhangke.fread.explore.screens.search.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.commonbiz.shared.composable.SearchResultUi
import com.zhangke.fread.explore.ExplorerElements
import com.zhangke.fread.explore.R
import com.zhangke.fread.explore.screens.search.SearchScreen
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class, InternalVoyagerApi::class)
@Composable
fun Screen.ExplorerSearchBar(
    selectedAccount: LoggedAccount?,
    accountList: List<LoggedAccount>,
    onAccountSelected: (LoggedAccount) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow.rootNavigator
    var active by remember {
        mutableStateOf(false)
    }
    val viewModel = getViewModel<SearchBarViewModel>()
    viewModel.selectedAccount = selectedAccount
    val uiState by viewModel.uiState.collectAsState()
    if (!active) {
        LaunchedEffect(Unit) {
            viewModel.onSearchQueryChanged("")
        }
    }
    SearchBar(
        modifier = Modifier
            .searchPadding(active)
            .fillMaxWidth()
            .onFocusChanged {
                if (it.hasFocus && !active) {
                    active = true
                }
            },
        windowInsets = WindowInsets(0, 0, 0, 0),
        leadingIcon = {
            if (active) {
                Toolbar.BackButton(onBackClick = { active = false })
            } else {
                SimpleIconButton(
                    onClick = { active = true },
                    imageVector = Icons.Default.Search,
                    contentDescription = "Back",
                )
            }
        },
        trailingIcon = {
            SearchBarTrailing(
                active = active,
                selectedAccount = selectedAccount,
                onClearClick = { viewModel.onSearchQueryChanged("") },
                accountList = accountList,
                onAccountSelected = onAccountSelected,
            )
        },
        query = uiState.query,
        placeholder = {
            if (selectedAccount != null && accountList.size > 1) {
                Text(
                    text = stringResource(
                        R.string.explorer_search_bar_hint_specialize_platform,
                        selectedAccount.platform.baseUrl.host,
                    ),
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                Text(
                    text = stringResource(R.string.explorer_search_bar_hint),
                )
            }
        },
        onQueryChange = viewModel::onSearchQueryChanged,
        onSearch = {
            active = false
            navigator.push(SearchScreen(uiState.role, uiState.query))
        },
        active = active,
        onActiveChange = {
            active = it
            reportClick(ExplorerElements.SEARCH) {
                put("active", "$it")
            }
        },
    ) {
        if (active) {
            BackHandler(true) {
                active = false
            }
            SearchContent(
                uiState = uiState,
                snackbarMessageFlow = viewModel.errorMessageFlow,
                composedStatusInteraction = viewModel.composedStatusInteraction,
            )
        }
    }
    ConsumeFlow(viewModel.openScreenFlow) {
        navigator.push(it)
    }
}

@Composable
private fun SearchContent(
    uiState: SearchBarUiState,
    snackbarMessageFlow: Flow<TextString>,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val state = rememberLazyListState()
        InlineVideoLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = state,
        ) {
            itemsIndexed(uiState.resultList) { index, item ->
                SearchResultUi(
                    modifier = Modifier.fillMaxWidth(),
                    searchResult = item,
                    indexInList = index,
                    composedStatusInteraction = composedStatusInteraction,
                )
            }
        }

        val snackbarHostState = rememberSnackbarHostState()
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.Center),
        )
        ConsumeSnackbarFlow(snackbarHostState, snackbarMessageFlow)
    }
}

@Composable
private fun SearchBarTrailing(
    active: Boolean,
    selectedAccount: LoggedAccount?,
    onClearClick: () -> Unit,
    accountList: List<LoggedAccount>,
    onAccountSelected: (LoggedAccount) -> Unit,
) {
    if (active) {
        SimpleIconButton(
            onClick = onClearClick,
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear Query",
        )
    } else {
        if (accountList.size > 1) {
            var showSelectAccountPopup by remember {
                mutableStateOf(false)
            }
            Row(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(23.dp),
                    )
                    .noRippleClick { showSelectAccountPopup = true },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Account",
                )
                Spacer(modifier = Modifier.width(4.dp))
                BlogAuthorAvatar(
                    modifier = Modifier.size(40.dp),
                    imageUrl = selectedAccount?.avatar,
                )
            }
            DropdownMenu(
                modifier = Modifier,
                expanded = showSelectAccountPopup,
                onDismissRequest = { showSelectAccountPopup = false },
            ) {
                accountList.forEach { account ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                BlogAuthorAvatar(
                                    modifier = Modifier.size(32.dp),
                                    imageUrl = account.avatar,
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    modifier = Modifier.width(100.dp),
                                    text = account.userName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                RadioButton(
                                    selected = selectedAccount == account,
                                    onClick = {
                                        onAccountSelected(account)
                                        showSelectAccountPopup = false
                                    },
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                        },
                        onClick = {
                            showSelectAccountPopup = false
                            onAccountSelected(account)
                        },
                    )
                }
            }
        }
    }
}

private fun Modifier.searchPadding(active: Boolean): Modifier {
    return if (active) {
        this
    } else {
        Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp) then this
    }
}
