package com.zhangke.fread.explore.screens.search.bar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
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
import com.zhangke.fread.commonbiz.shared.composable.SearchResultUi
import com.zhangke.fread.explore.Res
import com.zhangke.fread.explore.explorer_search_bar_hint
import com.zhangke.fread.explore.explorer_search_bar_hint_specialize_platform
import com.zhangke.fread.explore.screens.search.SearchScreen
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.common.SelectAccountDialog
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, InternalVoyagerApi::class)
@Composable
fun Screen.ExplorerSearchBar(
    selectedAccount: LoggedAccount?,
    accountList: List<LoggedAccount>,
    onAccountSelected: (LoggedAccount) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    var active by rememberSaveable { mutableStateOf(false) }
    val viewModel = getViewModel<SearchBarViewModel>()
    LaunchedEffect(selectedAccount) {
        viewModel.selectedAccount = selectedAccount
    }
    val uiState by viewModel.uiState.collectAsState()
    var horizontalPaddingDp by remember { mutableStateOf(16) }
    if ((active && horizontalPaddingDp != 0) || (!active && horizontalPaddingDp != 16)) {
        LaunchedEffect(Unit) {
            Animatable(
                initialValue = horizontalPaddingDp.toFloat()
            ).animateTo(
                targetValue = if (active) 0F else 16F,
                animationSpec = tween(durationMillis = 180),
            ) {
                horizontalPaddingDp = value.roundToInt()
            }
        }
    }
    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPaddingDp.dp),
        windowInsets = WindowInsets.statusBars,
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.onFocusChanged {
                    if (it.hasFocus && !active) {
                        active = true
                    }
                },
                query = uiState.query,
                onQueryChange = viewModel::onSearchQueryChanged,
                onSearch = {
                    uiState.locator?.let {
                        navigator.push(SearchScreen(it, uiState.query))
                    }
                },
                expanded = active,
                onExpandedChange = { active = it },
                placeholder = {
                    if (selectedAccount != null && accountList.size > 1) {
                        Text(
                            modifier = Modifier,
                            text = stringResource(
                                Res.string.explorer_search_bar_hint_specialize_platform,
                                selectedAccount.platform.baseUrl.host,
                            ),
                            overflow = TextOverflow.Ellipsis,
                        )
                    } else {
                        Text(
                            modifier = Modifier,
                            text = stringResource(Res.string.explorer_search_bar_hint),
                        )
                    }
                },
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
            )
        },
        expanded = active,
        onExpandedChange = { active = it },
        content = {
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
        },
    )
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
            if (showSelectAccountPopup) {
                SelectAccountDialog(
                    accountList = accountList,
                    selectedAccounts = selectedAccount?.let { listOf(it) } ?: emptyList(),
                    onDismissRequest = { showSelectAccountPopup = false },
                    onAccountClicked = onAccountSelected,
                )
            }
        }
    }
}
