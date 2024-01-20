package com.zhangke.utopia.feature.message.screens.home

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel

class NotificationsHomeScreen: Screen {

    @Composable
    override fun Content() {
        val viewModel: NotificationsHomeViewModel = getViewModel()

    }
}
