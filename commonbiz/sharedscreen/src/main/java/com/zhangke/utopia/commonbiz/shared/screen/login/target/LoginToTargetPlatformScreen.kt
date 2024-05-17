package com.zhangke.utopia.commonbiz.shared.screen.login.target

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.Toolbar
import com.zhangke.utopia.commonbiz.shared.screen.R
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.ui.BlogPlatformUi

class LoginToTargetPlatformScreen(val platform: BlogPlatform) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val viewModel =
            getViewModel<LoginToTargetPlatformViewModel, LoginToTargetPlatformViewModel.Factory> {
                it.create(platform)
            }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Toolbar(title = stringResource(R.string.login_dialog_target_title))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BlogPlatformUi(
                    modifier = Modifier
                        .clickable { viewModel.onServerHostConfirmClick() },
                    platform = platform,
                    showDivider = false,
                )
            }
        }
    }
}
