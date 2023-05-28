package com.zhangke.utopia.pages.sources.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zhangke.utopia.R
import com.zhangke.utopia.composable.*
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSource
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSourceUiState
import com.zhangke.utopia.pages.sources.add.search.searchSourceForAddRouter

const val addSourceRoute = "source/add?addUris={addUris}"

fun NavGraphBuilder.addSourceRoute(navController: NavController) {
    composable(
        route = addSourceRoute,
        arguments = listOf(navArgument("addUris") { defaultValue = "" }),
    ) {
        val viewModel: AddSourceViewModel = hiltViewModel()
        val addUris = it.arguments?.getString("addUris")
        if (addUris.isNullOrEmpty().not()) {
            LaunchedEffect(addUris) {
                viewModel.onAddSources(addUris!!)
            }
        }
        AddSourcePage(
            uiState = viewModel.uiState.collectAsState().value,
            onAddSourceClick = {
                navController.navigate(searchSourceForAddRouter)
            },
            onConfirmClick = viewModel::onConfirmClick,
            onRemoveSourceClick = viewModel::onRemoveSource,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSourcePage(
    uiState: AddSourceUiState,
    onAddSourceClick: () -> Unit,
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
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSourceClick) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.Add),
                    contentDescription = "Add Source",
                )
            }
        }
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = inputtedText,
                    onValueChange = { inputtedText = it },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    ),
                )
            }

            LazyColumn {
                items(uiState.sourceList) { item ->
                    StatusSource(
                        modifier = Modifier.fillMaxWidth(),
                        source = item,
                        onRemoveClick = {
                            onRemoveSourceClick(item)
                        },
                    )
                }
            }
        }
    }
}
