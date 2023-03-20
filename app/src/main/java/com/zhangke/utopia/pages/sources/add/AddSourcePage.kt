package com.zhangke.utopia.pages.sources.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.R
import com.zhangke.utopia.composable.*
import com.zhangke.utopia.composable.source.maintainer.SourceMaintainer
import com.zhangke.utopia.status_provider.StatusSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSourcePage(
    uiState: AddSourceUiState,
    onSearchClick: (String) -> Unit,
    onAddSourceClick: (source: StatusSource) -> Unit,
    onConfirmClick: () -> Unit,
) {
    val snackbarHostState = rememberSnackbarHostState()
    ObserveSnackbar(snackbarHostState, uiState.errorMessage)
    Scaffold(
        topBar = {
            Toolbar(title = stringResource(id = R.string.search_page_title))
        },
        snackbarHost = snackbarHost(snackbarHostState),
    ) {
        if (uiState.pendingAdd) {
            InputSourceInfo(modifier = Modifier.padding(it), onSearchClick)
        } else if (uiState.searching) {
            LoadingPage()
        } else if (uiState.maintainer != null) {
            SourceMaintainer(
                uiState = uiState.maintainer,
                onAddSourceClick = onAddSourceClick,
                onConfirmClick = onConfirmClick,
            )
        }
    }
}

@Composable
fun InputSourceInfo(
    modifier: Modifier = Modifier,
    onSearchClick: (String) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            var inputtedContent by remember { mutableStateOf("") }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp),
                value = inputtedContent,
                onValueChange = {
                    inputtedContent = it
                }
            )

            Button(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = { onSearchClick(inputtedContent) }
            ) {
                Text(text = LocalContext.current.getString(R.string.add))
            }
        }
    }
}