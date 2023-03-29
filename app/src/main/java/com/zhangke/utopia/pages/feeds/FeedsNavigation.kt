package com.zhangke.utopia.pages.feeds

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.zhangke.utopia.pages.UtopiaRouters

context (UtopiaRouters)
fun NavGraphBuilder.registerFeedsNavigation() {
    navigation(
        startDestination = Feeds.container,
        route = Feeds.root,
    ) {
        composable(Feeds.container) {
            val viewModel: FeedsContainerViewModel = hiltViewModel()
            FeedsContainerPage(
                uiState = viewModel.uiState.collectAsState().value,
                onTabSelected = viewModel::onPageChanged,
            )
        }
    }
}