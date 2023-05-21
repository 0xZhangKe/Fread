package com.zhangke.utopia.pages.sources.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zhangke.utopia.R
import com.zhangke.utopia.composable.*
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSource
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSourceUiState
import com.zhangke.utopia.pages.sources.search.StatusOwnerAndSourceUiState
import com.zhangke.utopia.status.source.StatusSourceOwner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSourcePage(
    uiState: AddSourceUiState,
    onConfirmClick: (name: String) -> Unit,
    onRemoveSourceClick: (item: StatusSourceUiState) -> Unit,
) {
    val snackbarHostState = rememberSnackbarHostState()
    ObserveSnackbar(snackbarHostState, uiState.errorMessageText)
    var inputtedText by remember {
        mutableStateOf("")
    }
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(id = R.string.add_feeds_page_title),
                actions = {
                    IconButton(
                        modifier = Modifier.size(24.dp),
                        onClick = {
                            onConfirmClick(inputtedText)
                        },
                    ) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Default.PostAdd),
                            contentDescription = "Add",
                        )
                    }
                }
            )
        },
        snackbarHost = snackbarHost(snackbarHostState),
    ) { paddings ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = inputtedText,
                    onValueChange = { inputtedText = it }
                )
            }

            LazyColumn {
                items(uiState.sourceList) { item ->
                    StatusOwnerAndSource(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
                        uiState = item,
                        onRemoveClick = onRemoveSourceClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusOwnerAndSource(
    modifier: Modifier = Modifier,
    uiState: StatusOwnerAndSourceUiState,
    onRemoveClick: (source: StatusSourceUiState) -> Unit,
) {
    Card(modifier) {
        StatusOwner(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            owner = uiState.owner,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            uiState.sourceList.forEach {
                StatusSource(
                    modifier = Modifier.fillMaxWidth(),
                    source = it,
                    onRemoveClick = {
                        onRemoveClick(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusOwner(
    modifier: Modifier = Modifier,
    owner: StatusSourceOwner,
) {
    Box(
        modifier = modifier,
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = owner.thumbnail,
            contentDescription = "thumbnail",
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier,
                text = owner.name,
                maxLines = 1,
                fontSize = 18.sp,
            )
            Text(
                modifier = Modifier
                    .padding(top = 8.dp),
                text = owner.uri,
                maxLines = 1,
                fontSize = 14.sp,
            )
            Text(
                modifier = Modifier
                    .padding(top = 8.dp),
                text = owner.description,
                maxLines = 1,
                fontSize = 14.sp,
            )
        }
    }
}
