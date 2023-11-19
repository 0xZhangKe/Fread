package com.zhangke.utopia.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.utopia.profile.pages.home.ProfileHomePage
import com.zhangke.utopia.profile.pages.home.ProfileHomeViewModel
import com.zhangke.utopia.profile.pages.login.LoginScreen

object ProfileTab : Tab {

    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(Icons.Default.Settings)
            return remember {
                TabOptions(
                    index = 0u, title = "Profile", icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel = getViewModel<ProfileHomeViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        ProfileHomePage(
            uiState = uiState,
            onAddAccountClick = {
                bottomSheetNavigator.push(LoginScreen())
            },
        )
    }
}