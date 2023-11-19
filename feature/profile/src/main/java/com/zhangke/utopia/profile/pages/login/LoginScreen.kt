package com.zhangke.utopia.profile.pages.login

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.krouter.Destination
import com.zhangke.utopia.commonbiz.shared.router.SharedRouter

@Destination(SharedRouter.Profile.login)
class LoginScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val viewModel = getViewModel<LoginViewModel>()

    }
}
