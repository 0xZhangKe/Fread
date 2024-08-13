package com.zhangke.fread.feeds.pages.manager.add.pre

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.LoadingDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.analytics.PreAddContentElements
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.feeds.R
import com.zhangke.fread.feeds.pages.manager.importing.ImportFeedsScreen
import com.zhangke.fread.status.search.SearchContentResult
import com.zhangke.fread.status.ui.source.BlogPlatformSnapshotUi
import com.zhangke.fread.status.ui.source.BlogPlatformUi
import com.zhangke.fread.status.ui.source.StatusSourceUi

/**
 * 添加 Feeds 预先搜索页，用于输入内容，判断类型添加内容。
 */
class PreAddFeedsScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel = getScreenModel<PreAddFeedsViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = rememberSnackbarHostState()
        PreAddFeedsContent(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onBackClick = navigator::pop,
            onQueryChanged = viewModel::onQueryChanged,
            onSearchClick = {
                reportClick(PreAddContentElements.SEARCH)
                viewModel.onSearchClick()
            },
            onContentClick = {
                reportClick(PreAddContentElements.ITEM)
                viewModel.onContentClick(it)
            },
            onLoadingDismissRequest = viewModel::onLoadingDismissRequest,
            onImportClick = {
                reportClick(PreAddContentElements.IMPORT)
                navigator.push(ImportFeedsScreen())
            },
            onLoginDialogDismissRequest = viewModel::onLoginDialogDismissRequest,
            onCancelLoginDialogClick = {
                navigator.pop()
            },
            onLoginClick = {
                reportClick(PreAddContentElements.LOGIN)
                viewModel.onLoginClick()
            },
        )
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.replace(it)
        }
        ConsumeSnackbarFlow(snackBarHostState, viewModel.snackBarMessageFlow)
        val bottomSheetIsVisible = bottomSheetNavigator.isVisible
        var loginPageShown by remember {
            mutableStateOf(false)
        }
        if (bottomSheetIsVisible) {
            loginPageShown = true
        }
        if (loginPageShown && !bottomSheetIsVisible) {
            LaunchedEffect(Unit) {
                navigator.pop()
            }
        }
        ConsumeFlow(viewModel.exitScreenFlow) {
            navigator.pop()
        }
    }

    @Composable
    private fun PreAddFeedsContent(
        uiState: PreAddFeedsUiState,
        snackBarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onImportClick: () -> Unit,
        onQueryChanged: (String) -> Unit,
        onSearchClick: () -> Unit,
        onContentClick: (SearchContentResult) -> Unit,
        onLoadingDismissRequest: () -> Unit,
        onLoginDialogDismissRequest: () -> Unit,
        onCancelLoginDialogClick: () -> Unit,
        onLoginClick: () -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(id = R.string.add_feeds_page_title),
                    onBackClick = onBackClick,
                    actions = {
                        SimpleIconButton(
                            onClick = onImportClick,
                            imageVector = ImageVector.vectorResource(R.drawable.ic_import),
                            contentDescription = "Import",
                        )
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(snackBarHostState)
            },
        ) { innerPaddings ->
            Column(
                modifier = Modifier
                    .padding(innerPaddings)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp, top = 24.dp, end = 22.dp),
                    value = uiState.query,
                    onValueChange = onQueryChanged,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    placeholder = {
                        Text(text = stringResource(R.string.pre_add_feeds_hint))
                    },
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearchClick()
                        }
                    ),
                    trailingIcon = {
                        SimpleIconButton(
                            onClick = onSearchClick,
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                        )
                    }
                )
                if (uiState.searching) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 80.dp)
                                .size(80.dp)
                        )
                    }
                } else if (uiState.searchErrorMessage != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = uiState.searchErrorMessage)
                    }
                } else {
                    if (uiState.allSearchedResult.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp),
                        ) {
                            items(uiState.allSearchedResult) { content ->
                                SearchContentResultUi(content, onContentClick)
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 80.dp),
                                text = stringResource(id = R.string.pre_add_feeds_no_result),
                            )
                        }
                    }
                }
            }
            LoadingDialog(
                loading = uiState.loading,
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                ),
                onDismissRequest = onLoadingDismissRequest,
            )
        }

        if (uiState.showLoginDialog) {
            FreadDialog(
                onDismissRequest = onLoginDialogDismissRequest,
                contentText = stringResource(R.string.feeds_pre_add_login_dialog_content),
                onPositiveClick = {
                    onLoginDialogDismissRequest()
                    onLoginClick()
                },
                onNegativeClick = {
                    onLoginDialogDismissRequest()
                    onCancelLoginDialogClick()
                },
            )
        }
    }

    @Composable
    private fun SearchContentResultUi(
        content: SearchContentResult,
        onContentClick: (SearchContentResult) -> Unit,
    ) {
        when (content) {
            is SearchContentResult.Source -> StatusSourceUi(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onContentClick(content)
                    },
                source = content.source,
            )

            is SearchContentResult.ActivityPubPlatform -> BlogPlatformUi(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onContentClick(content) },
                platform = content.platform,
            )

            is SearchContentResult.ActivityPubPlatformSnapshot -> BlogPlatformSnapshotUi(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onContentClick(content) },
                platform = content.platform,
            )
        }
    }
}
