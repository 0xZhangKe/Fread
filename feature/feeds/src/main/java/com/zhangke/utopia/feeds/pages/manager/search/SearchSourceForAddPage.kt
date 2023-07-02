package com.zhangke.utopia.feeds.pages.manager.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.feeds.composable.StatusSourceNode
import com.zhangke.utopia.feeds.composable.StatusSourceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchSourceForAddPage(
    loadableState: LoadableState<List<StatusSourceUiState>>,
    onBackClick: () -> Unit,
    onSearchClick: (query: String) -> Unit,
    onAddClick: (StatusSourceUiState) -> Unit,
    onRemoveClick: (StatusSourceUiState) -> Unit,
) {
    BackHandler(true) {
        onBackClick()
    }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(68.dp)
                ) {
                    IconButton(
                        modifier = Modifier.align(Alignment.Center),
                        onClick = onBackClick
                    ) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Filled.ArrowBack),
                            "back"
                        )
                    }
                }
                var inputtedValue by remember {
                    mutableStateOf("")
                }
                TextField(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 25.dp)
                        .weight(1F),
                    value = inputtedValue,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearchClick(inputtedValue)
                        }
                    ),
                    onValueChange = {
                        inputtedValue = it
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    )
                )
                TextButton(
                    modifier = Modifier.padding(end = 15.dp),
                    onClick = { onSearchClick(inputtedValue) },
                ) {
                    Text(text = stringResource(com.zhangke.utopia.commonbiz.R.string.search))
                }
            }
        }
    ) {
        LoadableLayout(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            state = loadableState
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 15.dp),
            ) {
                items(it) { item ->
                    StatusSourceNode(
                        modifier = Modifier,
                        item,
                        onAddClick = {
                            onAddClick(item)
                        },
                        onRemoveClick = {
                            onRemoveClick(item)
                        }
                    )
                }
            }
        }
    }
}
