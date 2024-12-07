package com.zhangke.fread.bluesky.internal.screen.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.decodeAsUri
import com.zhangke.fread.bluesky.Res
import com.zhangke.fread.bluesky.bsky_add_content_hosting_provider
import com.zhangke.fread.bluesky.bsky_add_content_password
import com.zhangke.fread.bluesky.bsky_add_content_title
import com.zhangke.fread.bluesky.bsky_add_content_user_name
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.login
import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.RouteParam
import org.jetbrains.compose.resources.stringResource

@Destination(AddBlueskyContentRoute.ROUTE)
class AddBlueskyContentScreen(
    @RouteParam(AddBlueskyContentRoute.PARAMS_BASE_URL) private val baseUrl: String,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            getViewModel<AddBlueskyContentViewModel, AddBlueskyContentViewModel.Factory> {
                it.create(FormalBaseUrl.parse(baseUrl.decodeAsUri())!!)
            }
        val uiState by viewModel.uiState.collectAsState()
        AddBlueskyContentContent(
            uiState = uiState,
            onHostingChange = viewModel::onHostingChange,
            onBackClick = navigator::pop,
            onLoginClick = {

            },
        )
    }

    @Composable
    private fun AddBlueskyContentContent(
        uiState: AddBlueskyContentUiState,
        onHostingChange: (String) -> Unit,
        onBackClick: () -> Unit,
        onLoginClick: () -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.bsky_add_content_title),
                    onBackClick = onBackClick,
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    value = uiState.hosting,
                    onValueChange = onHostingChange,
                    label = {
                        Text(stringResource(Res.string.bsky_add_content_hosting_provider))
                    },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = uiState.username,
                    onValueChange = onHostingChange,
                    label = {
                        Text(stringResource(Res.string.bsky_add_content_user_name))
                    },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = uiState.password,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = onHostingChange,
                    label = {
                        Text(stringResource(Res.string.bsky_add_content_password))
                    },
                    singleLine = true,
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    onClick = onLoginClick,
                ) {
                    Text(stringResource(com.zhangke.fread.commonbiz.Res.string.login))
                }
            }
        }
    }
}
