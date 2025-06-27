package com.zhangke.fread.activitypub.app.internal.screen.user.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.utils.transparentColors
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_search_user_placeholder
import com.zhangke.fread.activitypub.app.internal.screen.list.AccountItem
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.PlatformLocator
import org.jetbrains.compose.resources.stringResource

class SearchUserScreen(
    private val locator: PlatformLocator,
    private val onlyFollowing: Boolean,
) : BaseScreen() {

    var onAccountSelected: ((ActivityPubAccountEntity) -> Unit)? = null

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewMode = getViewModel<SearchUserViewModel, SearchUserViewModel.Factory> {
            it.create(locator, onlyFollowing)
        }
        val snackbarHostState = rememberSnackbarHostState()
        val uiState by viewMode.uiState.collectAsState()
        SearchUserContent(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onAccountClicked = {
                onAccountSelected?.invoke(it)
                navigator.pop()
            },
            onQueryChange = viewMode::onQueryChange,
            onSearchClick = viewMode::onSearchClick,
            onBackClick = navigator::pop,
        )
        ConsumeSnackbarFlow(snackbarHostState, viewMode.snackBarMessage)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SearchUserContent(
        uiState: SearchUserUiState,
        snackbarHostState: SnackbarHostState,
        onAccountClicked: (ActivityPubAccountEntity) -> Unit,
        onQueryChange: (String) -> Unit,
        onSearchClick: () -> Unit,
        onBackClick: () -> Unit,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        val focusRequester = remember { FocusRequester() }
                        LaunchedEffect(Unit) { focusRequester.requestFocus() }
                        TextField(
                            modifier = Modifier.fillMaxWidth()
                                .focusRequester(focusRequester),
                            value = uiState.query,
                            onValueChange = { onQueryChange(it) },
                            placeholder = {
                                Text(
                                    text = stringResource(Res.string.activity_pub_search_user_placeholder)
                                )
                            },
                            keyboardActions = KeyboardActions(
                                onSearch = { onSearchClick() }
                            ),
                            singleLine = true,
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search
                            ),
                            colors = TextFieldDefaults.transparentColors.copy(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                            ),
                            textStyle = MaterialTheme.typography.titleMedium,
                        )
                    },
                    navigationIcon = {
                        Toolbar.BackButton(onBackClick = onBackClick)
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(uiState.accounts) {
                        AccountItem(
                            modifier = Modifier.clickable { onAccountClicked(it) },
                            account = it,
                            showRemoveIcon = false,
                        )
                    }
                }
                if (uiState.searching) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center).size(64.dp)
                    )
                }
            }
        }
    }
}
