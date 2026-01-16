package com.zhangke.fread.activitypub.app.internal.screen.filters.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.textString
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.activitypub.app.internal.screen.filters.edit.EditFilterScreenKey
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class FiltersListScreenKey(val locator: PlatformLocator) : NavKey

@Composable
fun FiltersListScreen(viewModel: FiltersListViewModel, locator: PlatformLocator) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = rememberSnackbarHostState()
    FiltersListContent(
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onBackClick = backStack::removeLastOrNull,
        onItemClick = {
            backStack.add(EditFilterScreenKey(locator, it.id))
        },
        onAddClick = {
            backStack.add(EditFilterScreenKey(locator, null))
        },
    )
    LaunchedEffect(Unit) {
        viewModel.onPageResume()
    }
    ConsumeSnackbarFlow(snackBarHostState, viewModel.snackBarFlow)
}

@Composable
private fun FiltersListContent(
    uiState: FiltersListUiState,
    snackBarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onItemClick: (FilterItemUiState) -> Unit,
    onAddClick: () -> Unit,
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        },
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.activity_pub_filters_list_page_title),
                onBackClick = onBackClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.surface,
                onClick = {
                    onAddClick()
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
    ) { innerPadding ->
        val state = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = state,
        ) {
            if (uiState.initializing && uiState.list.isEmpty()) {
                items(30) {
                    FilterItemPlaceholder()
                }
            } else if (uiState.list.isNotEmpty()) {
                items(uiState.list) {
                    FilterItem(
                        filterEntity = it,
                        onItemClick = onItemClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterItemPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 18.dp)
                .freadPlaceholder(true)
        )

        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(width = 180.dp, height = 16.dp)
                .freadPlaceholder(true)
        )
    }
}

@Composable
private fun FilterItem(
    filterEntity: FilterItemUiState,
    onItemClick: (FilterItemUiState) -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable { onItemClick(filterEntity) }
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = filterEntity.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = textString(text = filterEntity.validateDescription),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}