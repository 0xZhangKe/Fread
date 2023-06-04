package com.zhangke.utopia.pages.feeds

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.zhangke.utopia.pages.feeds.container.FeedsContainerPage
import com.zhangke.utopia.pages.feeds.container.FeedsContainerViewModel
import com.zhangke.utopia.pages.sources.add.addSourceRoute

fun NavGraphBuilder.registerFeedsNavigation(navController: NavController) {
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
                onAddFeedsClick = {
                    navController.navigate(addSourceRoute)
                },
                onRefresh = {
                    viewModel.onPageChanged(0)
                },
            )
        }
    }
}
