package com.zhangke.fread.feeds.pages.manager.add.pre

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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.hilt.getViewModel
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
import com.zhangke.framework.utils.HighlightTextBuildUtil
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.feeds.Res
import com.zhangke.fread.feeds.add_feeds_page_title
import com.zhangke.fread.feeds.feeds_pre_add_login_dialog_content
import com.zhangke.fread.feeds.ic_import
import com.zhangke.fread.feeds.pages.manager.importing.ImportFeedsScreen
import com.zhangke.fread.feeds.pre_add_feeds_hint
import com.zhangke.fread.feeds.pre_add_feeds_input_label_1
import com.zhangke.fread.feeds.pre_add_feeds_input_label_2
import com.zhangke.fread.feeds.pre_add_feeds_no_result
import com.zhangke.fread.status.search.SearchContentResult
import com.zhangke.fread.status.ui.source.SearchContentResultUi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * 添加 Feeds 预先搜索页，用于输入内容，判断类型添加内容。
 */
class PreAddFeedsScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel = getViewModel<PreAddFeedsViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = rememberSnackbarHostState()
        PreAddFeedsContent(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onBackClick = navigator::pop,
            onQueryChanged = viewModel::onQueryChanged,
            onSearchClick = viewModel::onSearchClick,
            onContentClick = viewModel::onContentClick,
            onLoadingDismissRequest = viewModel::onLoadingDismissRequest,
            onImportClick = {
                navigator.push(ImportFeedsScreen())
            },
            onLoginDialogDismissRequest = viewModel::onLoginDialogDismissRequest,
            onCancelLoginDialogClick = {
                navigator.pop()
            },
            onLoginClick = {
                viewModel.onLoginClick()
            },
        )
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.push(it)
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
                    title = stringResource(Res.string.add_feeds_page_title),
                    onBackClick = onBackClick,
                    actions = {
                        SimpleIconButton(
                            onClick = onImportClick,
                            imageVector = vectorResource(Res.drawable.ic_import),
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
                        .padding(start = 16.dp, top = 18.dp, end = 16.dp),
                    value = uiState.query,
                    onValueChange = onQueryChanged,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.pre_add_feeds_hint),
                            style = MaterialTheme.typography.bodyMedium,
                        )
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
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            if (uiState.query.isEmpty()) {
                                item {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = 16.dp,
                                                top = 12.dp,
                                                end = 16.dp,
                                                bottom = 8.dp,
                                            ),
                                        lineHeight = 22.sp,
                                        text = buildInputLabelText(),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
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
                                text = stringResource(Res.string.pre_add_feeds_no_result),
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
                contentText = stringResource(Res.string.feeds_pre_add_login_dialog_content),
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
    private fun buildInputLabelText(): AnnotatedString {
        return buildAnnotatedString {
            append(
                HighlightTextBuildUtil.buildHighlightText(
                    text = stringResource(Res.string.pre_add_feeds_input_label_1),
                    fontWeight = FontWeight.Bold,
                    highLightSize = 14.sp,
                )
            )
            append(
                HighlightTextBuildUtil.buildHighlightText(
                    text = stringResource(Res.string.pre_add_feeds_input_label_2),
                    fontWeight = FontWeight.Bold,
                    highLightSize = 14.sp,
                    highLightColor = MaterialTheme.colorScheme.tertiary,
                )
            )
        }
    }
}
