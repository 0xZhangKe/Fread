package com.zhangke.fread.feeds.pages.manager.search

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.utils.HighlightTextBuildUtil
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.feeds.composable.StatusSourceNode
import com.zhangke.fread.feeds.composable.StatusSourceUiState
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.source.StatusSource
import org.jetbrains.compose.resources.stringResource
import kotlin.jvm.Transient

internal class SearchSourceForAddScreen : BaseScreen() {

    companion object {

        internal const val SCREEN_KEY =
            "com.zhangke.fread.feeds.pages.manager.search.SearchSourceForAddScreen"
    }

    @Transient
    var onSourceSelected: ((StatusSource) -> Unit)? = null

    override val key: ScreenKey
        get() = SCREEN_KEY

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: SearchSourceForAddViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val snackBarHostState = rememberSnackbarHostState()
        SearchSourceForAdd(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onBackClick = navigator::pop,
            onQueryChanged = viewModel::onQueryChanged,
            onSearchClick = viewModel::onSearchClick,
            onAddClick = {
                onSourceSelected?.invoke(it.source)
                navigator.pop()
            },
        )
        ConsumeSnackbarFlow(snackBarHostState, viewModel.snackbarMessageFlow)
    }

    @OptIn(InternalVoyagerApi::class)
    @Composable
    internal fun SearchSourceForAdd(
        uiState: SearchForAddUiState,
        snackBarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onQueryChanged: (String) -> Unit,
        onSearchClick: () -> Unit,
        onAddClick: (StatusSourceUiState) -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(LocalizedString.searchFeedsTitle),
                    onBackClick = onBackClick,
                )
            },
            snackbarHost = {
                SnackbarHost(snackBarHostState)
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
                            text = stringResource(LocalizedString.searchFeedsTitleHint),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    keyboardActions = KeyboardActions(
                        onSearch = { onSearchClick() }
                    ),
                    trailingIcon = {
                        if (uiState.searching) {
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
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 12.dp, end = 16.dp),
                    text = buildInputLabelText(),
                    lineHeight = 1.5.em,
                    style = MaterialTheme.typography.labelMedium,
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                ) {
                    items(uiState.searchedList) { item ->
                        StatusSourceNode(
                            modifier = Modifier,
                            onClick = { onAddClick(item) },
                            source = item,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun buildInputLabelText(): AnnotatedString {
        return buildAnnotatedString {
            append(
                HighlightTextBuildUtil.buildHighlightText(
                    text = stringResource(LocalizedString.preAddFeedsInputLabel1),
                    fontWeight = FontWeight.Bold,
                )
            )
            append(
                HighlightTextBuildUtil.buildHighlightText(
                    text = stringResource(LocalizedString.preAddFeedsInputLabel2),
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }
}
