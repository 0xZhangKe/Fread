package com.zhangke.utopia.feeds.pages.manager.search

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zhangke.utopia.feeds.feedsModuleRoute
import com.zhangke.utopia.feeds.pages.manager.FeedsManagerRoute
import java.net.URLEncoder

internal class SearchSourceForAddPageRoute {
    val route = "$feedsModuleRoute/manager/search"
}

internal fun NavGraphBuilder.searchSourceForAddRoute(navController: NavController) {
    composable(
        route = SearchSourceForAddPageRoute().route,
    ) {
        val viewModel: SearchSourceForAddViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsState().value
        SearchSourceForAddPage(
            loadableState = uiState.searchedResult,
            onBackClick = {
                val arguments = uiState.addedSourceUriList.joinToString(",")
                val encodedArguments = URLEncoder.encode(arguments, "UTF-8")
                val route = FeedsManagerRoute().route
                    .replace("{addUris}", encodedArguments)
                navController.popBackStack()
                navController.navigate(route) {
                    launchSingleTop = true
                }
            },
            onSearchClick = viewModel::onSearchClick,
            onAddClick = viewModel::onAddClick,
            onRemoveClick = viewModel::onRemoveClick,
        )
    }
}
