package com.zhangke.fread.commonbiz.shared.screen.login.target

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.login_dialog_target_title
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.ui.source.BlogPlatformUi
import org.jetbrains.compose.resources.stringResource

class LoginToTargetPlatformScreen(val platform: BlogPlatform) : BaseScreen() {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalBottomSheetNavigator.current
        val viewModel =
            getViewModel<LoginToTargetPlatformViewModel, LoginToTargetPlatformViewModel.Factory> {
                it.create(platform)
            }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Toolbar(title = stringResource(Res.string.login_dialog_target_title))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BlogPlatformUi(
                    modifier = Modifier
                        .clickable {
                            navigator.hide()
                            viewModel.onServerHostConfirmClick()
                        },
                    platform = platform,
                    showDivider = false,
                )
            }
        }
    }
}
