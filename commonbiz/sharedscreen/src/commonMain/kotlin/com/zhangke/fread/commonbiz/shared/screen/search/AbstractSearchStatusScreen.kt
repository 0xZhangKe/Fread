package com.zhangke.fread.commonbiz.shared.screen.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.keyboardAsState
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.loadable.lazycolumn.ObserveLoadMore
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.utils.transparentIndicatorAndContainerColors
import com.zhangke.fread.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
fun AbstractSearchStatusScreen(viewModel: AbstractSearchStatusViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val snackbarHostState = rememberSnackbarHostState()
    val uiState by viewModel.uiState.collectAsState()
    SearchStatusContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        composedStatusInteraction = viewModel.composedStatusInteraction,
        onBackClick = backStack::removeLastOrNull,
        onQueryChanged = viewModel::onQueryChange,
        onSearchClick = viewModel::onSearchClick,
        onLoadMore = viewModel::onLoadMore,
    )
    ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    ConsumeOpenScreenFlow(viewModel.openScreenFlow)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchStatusContent(
    uiState: SearchStatusUiState,
    snackbarHostState: SnackbarHostState,
    composedStatusInteraction: ComposedStatusInteraction,
    onBackClick: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onSearchClick: () -> Unit,
    onLoadMore: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardState by keyboardAsState()
    LaunchedEffect(keyboardState) {
        if (!keyboardState) {
            focusManager.clearFocus()
        }
    }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = { Toolbar.BackButton(onBackClick = onBackClick) },
                title = {
                    TextField(
                        modifier = Modifier.fillMaxWidth()
                            .focusRequester(focusRequester),
                        value = uiState.query,
                        onValueChange = onQueryChanged,
                        placeholder = {
                            Text(
                                text = stringResource(LocalizedString.statusUiSearchAccountStatusHint),
                            )
                        },
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                onSearchClick()
                            }
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search
                        ),
                        trailingIcon = {
                            if (uiState.searching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        },
                        colors = TextFieldDefaults.transparentIndicatorAndContainerColors,
                        maxLines = 1,
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val listState = rememberLazyListState()
        ObserveLoadMore(
            lazyListState = listState,
            onLoadMore = onLoadMore,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = listState,
        ) {
            itemsIndexed(uiState.result) { index, status ->
                FeedsStatusNode(
                    status = status,
                    indexInList = index,
                    composedStatusInteraction = composedStatusInteraction,
                )
            }
        }
    }
}
