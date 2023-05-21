package com.zhangke.utopia.pages.feeds

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.registerFeedsNavigation() {
    val sourceRouter = FeedsRouters()
    navigation(
        startDestination = sourceRouter.container,
        route = sourceRouter.root,
    ) {
        composable(sourceRouter.container) {
            val viewModel: FeedsContainerViewModel = hiltViewModel()
            FeedsContainerPage(
                uiState = viewModel.uiState.collectAsState().value,
                onTabSelected = viewModel::onPageChanged,
            )
        }
    }
}
