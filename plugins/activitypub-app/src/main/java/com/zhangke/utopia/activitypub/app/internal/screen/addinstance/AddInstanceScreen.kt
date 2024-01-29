package com.zhangke.utopia.activitypub.app.internal.screen.addinstance

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.textString
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.krouter.Destination
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.screen.instance.InstanceDetailScreen
import com.zhangke.utopia.activitypub.app.internal.screen.instance.PlatformDetailRoute
import com.zhangke.utopia.commonbiz.shared.screen.login.LoginBottomSheetScreen

@Destination(AddInstanceScreenRoute.ROOT)
class AddInstanceScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetDialogNavigator = LocalBottomSheetNavigator.current
        val navigationResult = navigator.navigationResult
        val viewModel: AddInstanceViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        AddInstanceContent(
            uiState = uiState,
            onQueryChanged = viewModel::onQueryInput,
            onBackClick = {
                if (uiState.inInstanceDetailPage) {
                    viewModel.onBackClick()
                } else {
                    navigator.pop()
                }
            },
            onSearchClick = viewModel::onSearchClick,
            onErrorMessageDismiss = viewModel::onErrorMessageDismiss,
            onConfirmClick = viewModel::onConfirmClick,
        )
        ConsumeFlow(viewModel.contentConfigFlow) {
            navigationResult.popWithResult(it)
        }
        ConsumeFlow(viewModel.openLoginFlow) {
            bottomSheetDialogNavigator.show(LoginBottomSheetScreen(it))
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun AddInstanceContent(
        uiState: AddInstanceUiState,
        onQueryChanged: (String) -> Unit,
        onSearchClick: () -> Unit,
        onBackClick: () -> Unit,
        onErrorMessageDismiss: () -> Unit,
        onConfirmClick: () -> Unit,
    ) {
        val state = rememberPagerState(
            pageCount = { 2 },
        )
        LaunchedEffect(uiState.inInstanceDetailPage) {
            if (uiState.inInstanceDetailPage) {
                state.animateScrollToPage(1)
            } else {
                state.animateScrollToPage(0)
            }
        }
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize(),
            state = state,
            userScrollEnabled = false,
        ) { pageIndex ->
            if (pageIndex == 0) {
                AddInstanceInputServerContent(
                    errorMessageText = uiState.errorMessage,
                    searching = uiState.searching,
                    query = uiState.query ?: "",
                    onQueryChanged = onQueryChanged,
                    onSearchClick = onSearchClick,
                    onBackClick = onBackClick,
                    onErrorMessageDismiss = onErrorMessageDismiss,
                )
            } else {
                val route = PlatformDetailRoute.buildRoute(uiState.instance!!.baseUrl, true)
                Navigator(
                    screen = InstanceDetailScreen(route),
                ) {
                    CurrentScreen()
                    val added by it.navigationResult.getResult<Boolean>(it.lastItem.key)
                    if (added != null) {
                        LaunchedEffect(added) {
                            if (added!!) {
                                onConfirmClick()
                            } else {
                                onBackClick()
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AddInstanceInputServerContent(
        errorMessageText: TextString?,
        searching: Boolean,
        query: String,
        onQueryChanged: (String) -> Unit,
        onSearchClick: () -> Unit,
        onBackClick: () -> Unit,
        onErrorMessageDismiss: () -> Unit,
    ) {
        val snackbarState = rememberSnackbarHostState()
        if (errorMessageText != null) {
            val errorMessage = textString(errorMessageText)
            LaunchedEffect(errorMessage) {
                snackbarState.showSnackbar(
                    message = errorMessage,
                )
                onErrorMessageDismiss()
            }
        }
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarState)
            },
            topBar = {
                Toolbar(
                    title = stringResource(R.string.add_instance_screen_title),
                    onBackClick = onBackClick,
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        value = query,
                        onValueChange = onQueryChanged,
                        placeholder = {
                            Text(text = stringResource(R.string.add_instance_input_service_hint))
                        },
                        enabled = !searching,
                        trailingIcon = {
                            if (searching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                SimpleIconButton(
                                    onClick = onSearchClick,
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                onSearchClick()
                            }
                        ),
                    )
                }
            }
        }
    }
}
