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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.ScreenEventFlow
import com.zhangke.framework.utils.transparentIndicatorColors
import com.zhangke.fread.activitypub.app.internal.screen.list.AccountItem
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class SearchUserScreenNavKey(
    val locator: PlatformLocator,
    val onlyFollowing: Boolean,
) : NavKey {

    companion object {
        val accountSelectedFlow = ScreenEventFlow<ActivityPubAccountEntity>()
    }
}

@Composable
fun SearchUserScreen(viewModel: SearchUserViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val snackbarHostState = rememberSnackbarHostState()
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    SearchUserContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAccountClicked = {
            coroutineScope.launch {
                SearchUserScreenNavKey.accountSelectedFlow.emit(it)
                backStack.removeLastOrNull()
            }
        },
        onQueryChange = viewModel::onQueryChange,
        onSearchClick = viewModel::onSearchClick,
        onBackClick = backStack::removeLastOrNull,
    )
    ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarMessage)
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
                                text = stringResource(LocalizedString.activity_pub_search_user_placeholder)
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
                        colors = TextFieldDefaults.transparentIndicatorColors.copy(
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
