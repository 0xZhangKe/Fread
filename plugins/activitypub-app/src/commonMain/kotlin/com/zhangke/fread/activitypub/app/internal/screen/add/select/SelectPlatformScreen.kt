package com.zhangke.fread.activitypub.app.internal.screen.add.select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LoadingDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_select_platform_text_hint
import com.zhangke.fread.activitypub.app.activity_pub_select_platform_title
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.ui.source.BlogPlatformSnapshotUi
import com.zhangke.fread.status.ui.source.BlogPlatformUi
import org.jetbrains.compose.resources.stringResource

class SelectPlatformScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<SelectPlatformViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = rememberSnackbarHostState()
        SelectPlatformContent(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onBackClick = navigator::pop,
            onQueryChanged = viewModel::onQueryChanged,
            onSearchClick = viewModel::onSearchClick,
            onPlatformClick = viewModel::onResultClick,
        )
        ConsumeOpenScreenFlow(viewModel.openNewPageFlow)
        ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarMessage)
        LoadingDialog(
            loading = uiState.loadingPlatformForAdd,
            onDismissRequest = viewModel::onLoadingPlatformForAddCancel,
        )
    }

    @Composable
    private fun SelectPlatformContent(
        uiState: SelectPlatformUiState,
        snackbarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onQueryChanged: (String) -> Unit,
        onSearchClick: () -> Unit,
        onPlatformClick: (SearchPlatformResult) -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.activity_pub_select_platform_title),
                    onBackClick = onBackClick,
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 18.dp, end = 16.dp),
                    value = uiState.query,
                    onValueChange = onQueryChanged,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.activity_pub_select_platform_text_hint),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    keyboardActions = KeyboardActions(
                        onSearch = { onSearchClick() }
                    ),
                    trailingIcon = {
                        if (uiState.querying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        } else {
                            SimpleIconButton(
                                onClick = onSearchClick,
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                            )
                        }
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.searchedResult.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(uiState.searchedResult) {
                            SearchPlatformResultUi(
                                result = it,
                                onClick = onPlatformClick,
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(uiState.platformSnapshotList) {
                            SearchPlatformResultUi(
                                result = it,
                                onClick = onPlatformClick,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SearchPlatformResultUi(
        result: SearchPlatformResult,
        onClick: (SearchPlatformResult) -> Unit,
    ) {
        when (result) {
            is SearchPlatformResult.SearchedPlatform -> BlogPlatformUi(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(result) },
                platform = result.platform,
            )

            is SearchPlatformResult.SearchedSnapshot -> BlogPlatformSnapshotUi(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(result) },
                platform = result.snapshot,
            )
        }
    }
}
