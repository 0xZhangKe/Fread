package com.zhangke.utopia.profile.pages.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.zhangke.framework.composable.CardInfoSection
import com.zhangke.framework.composable.Toolbar
import com.zhangke.krouter.Destination
import com.zhangke.utopia.commonbiz.shared.router.SharedRouter
import com.zhangke.utopia.profile.R
import com.zhangke.utopia.status.platform.BlogPlatform

@Destination(SharedRouter.Profile.login)
class LoginScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalBottomSheetNavigator.current
        val viewModel = getViewModel<LoginViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        var showInputContent by remember {
            mutableStateOf(false)
        }

        AnimatedVisibility(visible = !showInputContent) {
            LoginScreenContent(
                uiState = uiState,
                onPlatformClick = {
                    viewModel.onServerHostConfirmClick(it.baseUrl)
                    navigator.hide()
                },
                onInputClick = { showInputContent = true },
            )
        }
        AnimatedVisibility(visible = showInputContent) {
            InputServerHostContent(
                onBackClick = { showInputContent = false },
                onConfirmClick = {
                    viewModel.onServerHostConfirmClick(it)
                    navigator.hide()
                },
            )
        }
    }

    @Composable
    private fun LoginScreenContent(
        uiState: LoginUiState,
        onPlatformClick: (BlogPlatform) -> Unit,
        onInputClick: () -> Unit,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Toolbar(
                title = stringResource(R.string.login_dialog_title),
            )

            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                TextButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = onInputClick,
                ) {
                    Text(text = stringResource(R.string.login_dialog_input_tip))
                }
            }

            uiState.platformList.forEach {
                BlogPlatformUi(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .clickable { onPlatformClick(it) },
                    platform = it,
                )
            }
        }
    }

    @Composable
    private fun BlogPlatformUi(
        modifier: Modifier,
        platform: BlogPlatform
    ) {
        CardInfoSection(
            modifier = modifier,
            avatar = platform.thumbnail,
            title = platform.name,
            description = platform.description,
        )
    }

    @Composable
    private fun InputServerHostContent(
        onBackClick: () -> Unit,
        onConfirmClick: (String) -> Unit,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Toolbar(
                title = stringResource(R.string.login_dialog_title),
                onBackClick = onBackClick,
            )

            var inputtedValue by remember {
                mutableStateOf("")
            }
            TextField(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 40.dp, bottom = 40.dp),
                value = inputtedValue,
                label = {
                    Text(text = stringResource(R.string.login_dialog_input_label))
                },
                onValueChange = { inputtedValue = it },
                keyboardActions = KeyboardActions(
                    onGo = {
                        if (inputtedValue.isNotBlank()) {
                            onConfirmClick(inputtedValue)
                        }
                    },
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Go
                ),
                trailingIcon = {
                    TextButton(
                        onClick = {
                            if (inputtedValue.isNotBlank()) {
                                onConfirmClick(inputtedValue)
                            }
                        }
                    ) {
                        Text(text = stringResource(com.zhangke.utopia.commonbiz.R.string.ok))
                    }
                }
            )

        }
    }
}
