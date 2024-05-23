package com.zhangke.utopia.commonbiz.shared.screen.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LoadingDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.utopia.commonbiz.shared.screen.R
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.platform.PlatformSnapshot
import com.zhangke.utopia.status.ui.source.BlogPlatformSnapshotUi
import com.zhangke.utopia.status.ui.source.BlogPlatformUi

class LoginBottomSheetScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalBottomSheetNavigator.current
        val viewModel = getViewModel<LoginViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        val snackbarHostState = rememberSnackbarHostState()
        LoginContent(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onQueryChanged = viewModel::onQueryChanged,
            onSnapshotClick = {
                viewModel.onSnapshotClick(it)
            },
            onPlatformClick = {
                viewModel.onPlatformClick(it)
            },
            onSearchClick = viewModel::onSearchClick,
        )

        LoadingDialog(loading = uiState.loading) {
            viewModel.onDismissRequest()
        }

        ConsumeFlow(viewModel.hideScreenFlow) {
            navigator.hide()
        }
        ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarMessageFlow)
    }

    @Composable
    private fun LoginContent(
        uiState: LoginUiState,
        snackbarHostState: SnackbarHostState,
        onQueryChanged: (String) -> Unit,
        onSearchClick: () -> Unit,
        onSnapshotClick: (PlatformSnapshot) -> Unit,
        onPlatformClick: (BlogPlatform) -> Unit,
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp * 0.9F
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    text = stringResource(R.string.login_dialog_target_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
                    text = stringResource(R.string.profile_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    value = uiState.query,
                    onValueChange = onQueryChanged,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    placeholder = {
                        Text(text = stringResource(R.string.login_dialog_input_hint))
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
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                ) {
                    items(uiState.platformList) { platform ->
                        when (platform) {
                            is SearchPlatformForLogin.Snapshot -> {
                                BlogPlatformSnapshotUi(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSnapshotClick(platform.snapshot) },
                                    platform = platform.snapshot,
                                )
                            }

                            is SearchPlatformForLogin.Platform -> {
                                BlogPlatformUi(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onPlatformClick(platform.platform) },
                                    platform = platform.platform,
                                )
                            }
                        }
                    }
                }
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}
