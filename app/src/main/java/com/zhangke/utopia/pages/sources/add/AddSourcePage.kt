package com.zhangke.utopia.pages.sources.add

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.UtopiaDialog
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.snackbarHost
import com.zhangke.framework.composable.textString
import com.zhangke.framework.ktx.CollectOnComposable
import com.zhangke.utopia.R
import com.zhangke.utopia.composable.*
import com.zhangke.utopia.pages.feeds.shared.source.StatusSourceNode
import com.zhangke.utopia.pages.feeds.shared.source.StatusSourceUiState
import com.zhangke.utopia.pages.sources.add.search.searchSourceForAddRouter
import kotlinx.coroutines.flow.Flow

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
            errorMessageFlow = viewModel.errorMessageFlow,
            onAddSourceClick = {
                navController.navigate(searchSourceForAddRouter)
            },
            onConfirmClick = viewModel::onConfirmClick,
            onNameInputValueChanged = viewModel::onSourceNameInput,
            onRemoveSourceClick = viewModel::onRemoveSource,
            onChooseSourceItemClick = viewModel::onAuthItemClick,
            onChooseSourceDialogDismissRequest = viewModel::onChooseDialogDismissRequest,
        )
        viewModel.finishPage.CollectOnComposable {
            navController.popBackStack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSourcePage(
    uiState: AddSourceUiState,
    errorMessageFlow: Flow<TextString>,
    onAddSourceClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onNameInputValueChanged: (String) -> Unit,
    onRemoveSourceClick: (item: StatusSourceUiState) -> Unit,
    onChooseSourceItemClick: (StatusSourceUiState) -> Unit,
    onChooseSourceDialogDismissRequest: () -> Unit,
) {
    val snackbarHostState = rememberSnackbarHostState()
    ConsumeSnackbarFlow(snackbarHostState, errorMessageFlow)
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(id = R.string.add_feeds_page_title),
                actions = {
                    IconButton(
                        modifier = Modifier.size(24.dp),
                        onClick = onConfirmClick,
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
                    value = uiState.sourceName,
                    onValueChange = onNameInputValueChanged,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    ),
                )
            }

            LazyColumn(
                modifier = Modifier.padding(top = 15.dp)
            ) {
                items(uiState.sourceList) { item ->
                    StatusSourceNode(
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
    if (uiState.showChooseSourceDialog && uiState.invalidateSourceList.isNotEmpty()) {
        ChooseSourceDialog(
            uiState.invalidateSourceList,
            onChooseSourceItemClick,
            onChooseSourceDialogDismissRequest,
        )
    }
}

@Composable
private fun ChooseSourceDialog(
    sourceList: List<StatusSourceUiState>,
    onSourceItemClick: (StatusSourceUiState) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    UtopiaDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(id = R.string.add_feeds_choose_auth_dialog_title),
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = screenHeight)
            ) {
                items(sourceList) { item ->
                    StatusSourceNode(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSourceItemClick(item)
                            },
                        source = item,
                    )
                }
            }
        }
    )
}
