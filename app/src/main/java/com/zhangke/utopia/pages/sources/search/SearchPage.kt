package com.zhangke.utopia.pages.sources.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.R
import com.zhangke.utopia.composable.*
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSource
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSourceUiState
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceOwner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    uiState: SearchUiState,
    onSearchClick: (String) -> Unit,
    onAddSourceClick: (source: StatusSourceUiState) -> Unit,
) {
    val snackbarHostState = rememberSnackbarHostState()
    ObserveSnackbar(snackbarHostState, uiState.errorMessageText)
    Scaffold(
        topBar = {
            Toolbar(title = stringResource(id = R.string.search_page_title))
        },
        snackbarHost = snackbarHost(snackbarHostState),
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when (val searchedData = uiState.searchedData) {
                is LoadableState.Idle,
                is LoadableState.Failed,
                is LoadableState.Loading -> {
                    SearchContentInput(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        loading = searchedData is LoadableState.Loading,
                        onSearchClick = onSearchClick,
                    )
                }

                is LoadableState.Success -> {
                    StatusOwnerAndSourceList(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        owner = searchedData.data.owner,
                        sourceList = searchedData.data.sourceList,
                        onAddSourceClick = onAddSourceClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchContentInput(
    modifier: Modifier = Modifier,
    loading: Boolean,
    onSearchClick: (String) -> Unit
) {
    Column(modifier = modifier) {
        var inputtedContent by remember { mutableStateOf("") }
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            value = inputtedContent,
            singleLine = true,
            onValueChange = {
                inputtedContent = it
            }
        )

        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Button(
                    modifier = Modifier,
                    onClick = { onSearchClick(inputtedContent) }
                ) {
                    Text(text = LocalContext.current.getString(R.string.search))
                }
            }
        }
    }
}

@Composable
private fun StatusOwnerAndSourceList(
    modifier: Modifier = Modifier,
    owner: StatusSourceOwner,
    sourceList: List<StatusSourceUiState>,
    onAddSourceClick: (source: StatusSourceUiState) -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = 3.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5F)
                        .background(Color.Green),
                    model = owner.thumbnail,
                    contentScale = ContentScale.Crop,
                    contentDescription = "cover",
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x99000000)),
                            )
                        )
                        .padding(start = 15.dp, end = 15.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        modifier = Modifier.padding(top = 5.dp),
                        text = owner.name,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )

                    if (owner.uri.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = owner.uri,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                        text = owner.description,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
            Box(modifier = Modifier.height(10.dp))
            sourceList.forEach { source ->
                StatusSource(
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    source = source,
                    onAddClick = {
                        onAddSourceClick(source)
                    },
                )
            }
        }
    }
}
