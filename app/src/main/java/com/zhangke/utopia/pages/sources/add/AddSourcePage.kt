package com.zhangke.utopia.pages.sources.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zhangke.utopia.R
import com.zhangke.utopia.composable.*
import com.zhangke.utopia.composable.source.maintainer.SourceMaintainer
import com.zhangke.utopia.composable.source.maintainer.StatusSourceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSourcePage(
    uiState: AddSourceUiState,
    onSearchClick: (String) -> Unit,
    onAddSourceClick: (source: StatusSourceUiState) -> Unit,
    onConfirmClick: (String) -> Unit,
) {
    val snackbarHostState = rememberSnackbarHostState()
    ObserveSnackbar(snackbarHostState, uiState.errorMessageText)
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
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var dialogShow by remember {
                    mutableStateOf(false)
                }
                SourceMaintainer(
                    uiState = uiState.maintainer,
                    onSourceOptionClick = onAddSourceClick,
                )
                Button(
                    onClick = {
                        dialogShow = true
                    }
                ) {
                    Text(text = "Confirm")
                }
                if (dialogShow) {
                    InputNameDialog(
                        onConfirmClick = onConfirmClick,
                        onDismiss = {
                            dialogShow = false
                        },
                    )
                }
            }
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

@Composable
fun InputNameDialog(
    onConfirmClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "提示",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            var inputtedText by remember {
                mutableStateOf("")
            }
            TextField(
                value = inputtedText,
                onValueChange = {
                    inputtedText = it
                },
                label = {
                    Text(text = "Please enter channel name")
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    modifier = Modifier.padding(end = 15.dp),
                    onClick = onDismiss,
                ) {
                    Text(text = "Cancel")
                }
                Button(
                    onClick = {
                        onConfirmClick(inputtedText)
                    },
                ) {
                    Text(text = "Confirm")
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewNameInputDialog() {
    InputNameDialog(
        onConfirmClick = {},
        onDismiss = {},
    )
}