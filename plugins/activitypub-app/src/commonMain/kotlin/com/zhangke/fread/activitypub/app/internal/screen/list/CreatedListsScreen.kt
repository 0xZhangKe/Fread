package com.zhangke.fread.activitypub.app.internal.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.DefaultFailed
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.activitypub.app.internal.screen.list.add.AddListScreenNavKey
import com.zhangke.fread.activitypub.app.internal.screen.list.edit.EditListScreenNavKey
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class CreatedListsScreenKey(val locator: PlatformLocator) : NavKey

@Composable
fun CreatedListsScreen(
    viewModel: CreatedListsViewModel,
    locator: PlatformLocator,
) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    val snackBarState = rememberSnackbarHostState()
    CreatedListsContent(
        uiState = uiState,
        snackBarState = snackBarState,
        onBackClick = backStack::removeLastOrNull,
        onRetryClick = viewModel::onRetryClick,
        onListClick = {
            backStack.add(
                EditListScreenNavKey(
                    locator = locator,
                    serializedList = globalJson.encodeToString(it)
                )
            )
        },
        onAddListClick = { backStack.add(AddListScreenNavKey(locator)) },
    )
    LaunchedEffect(Unit) { viewModel.onPageResume() }
    ConsumeSnackbarFlow(snackBarState, viewModel.snackBarFlow)
}

@Composable
private fun CreatedListsContent(
    uiState: CreatedListsUiState,
    snackBarState: SnackbarHostState,
    onBackClick: () -> Unit,
    onAddListClick: () -> Unit,
    onListClick: (ActivityPubListEntity) -> Unit,
    onRetryClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.activity_pub_created_list_title),
                onBackClick = onBackClick,
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarState)
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.surface,
                onClick = onAddListClick,
            ) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.Add),
                    contentDescription = "Create List",
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            when {
                uiState.lists.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(uiState.lists) {
                            ListItem(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { onListClick(it) },
                                list = it,
                            )
                        }
                    }
                }

                uiState.loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        repeat(30) {
                            ListItemPlaceholder()
                        }
                    }
                }

                uiState.pageError != null -> {
                    DefaultFailed(
                        modifier = Modifier.fillMaxSize(),
                        exception = uiState.pageError,
                        onRetryClick = onRetryClick,
                    )
                }
            }
        }
    }
}
