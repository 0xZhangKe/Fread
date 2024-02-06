package com.zhangke.utopia.explore.screens.search.bar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.commonbiz.shared.composable.SearchResultUi
import com.zhangke.utopia.explore.R
import com.zhangke.utopia.explore.screens.search.SearchScreen
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen.ExplorerSearchBar() {
    val navigator = LocalNavigator.currentOrThrow.rootNavigator
    var active by remember {
        mutableStateOf(false)
    }
    val viewModel = getViewModel<SearchBarViewModel>()
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
            if (active) {
                SimpleIconButton(
                    onClick = { viewModel.onSearchQueryChanged("") },
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear Query",
                )
            }
        },
        query = uiState.query,
        placeholder = {
            Text(
                text = stringResource(R.string.explorer_search_bar_hint),
            )
        },
        onQueryChange = viewModel::onSearchQueryChanged,
        onSearch = {
            active = false
            navigator.push(SearchScreen(uiState.query))
        },
        active = active,
        onActiveChange = {
            active = it
        },
    ) {
        if (active) {
            BackHandler {
                active = false
            }
            SearchContent(
                uiState = uiState,
                snackbarMessageFlow = viewModel.errorMessageFlow,
                onUserInfoClick = viewModel::onUserInfoClick,
                onInteractive = viewModel::onInteractive,
                onHashtagClick = viewModel::onHashtagClick,
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
    onUserInfoClick: (BlogAuthor) -> Unit,
    onInteractive: (Status, StatusUiInteraction) -> Unit,
    onHashtagClick: (Hashtag) -> Unit,
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
                    onUserInfoClick = onUserInfoClick,
                    onInteractive = onInteractive,
                    onHashtagClick = onHashtagClick,
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

private fun Modifier.searchPadding(active: Boolean): Modifier {
    return if (active) {
        this
    } else {
        Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp) then this
    }
}
