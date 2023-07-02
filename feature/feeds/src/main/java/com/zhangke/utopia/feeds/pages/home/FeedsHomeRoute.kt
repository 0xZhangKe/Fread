package com.zhangke.utopia.feeds.pages.home

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zhangke.utopia.feeds.feedsModuleRoute

internal class FeedsHomePageRouter {

    val router = "$feedsModuleRoute/home"
}

internal fun NavGraphBuilder.feedsHomeRoute(navController: NavController) {
    composable(FeedsHomePageRouter().router) {
        val viewModel: FeedsHomeViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsState().value
        FeedsHomePage(
            uiState = uiState,
            onTabSelected = viewModel::onPageChanged,
            onLoadMore = viewModel::onLoadMore,
            onRefresh = viewModel::onRefresh,
            onAddFeedsClick = {

            },
        )
    }
}