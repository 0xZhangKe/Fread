package com.zhangke.utopia.profile.pages.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.CardInfoSection
import com.zhangke.krouter.Destination
import com.zhangke.utopia.commonbiz.shared.router.SharedRouter
import com.zhangke.utopia.profile.R
import com.zhangke.utopia.status.platform.BlogPlatform

@Destination(SharedRouter.Profile.login)
class LoginScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val viewModel = getViewModel<LoginViewModel>()
        val uiState by viewModel.uiState.collectAsState()
    }

    @Composable
    private fun LoginScreenContent(
        uiState: LoginUiState,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = stringResource(R.string.login_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { /*TODO*/ },
                ) {

                }
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
}
