package com.zhangke.utopia.pages.search

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.registerSearchPageNavigation(navController: NavController) {
    navigation(
        startDestination = "/search/input",
        route = "/search"
    ) {
        composable(
            route = "/search/input"
        ) {
            val viewModel: SearchViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsState().value
            SearchPage(
                uiState = uiState,
                onSearchClick = viewModel::onSearchClick,
                onAddSourceClick = viewModel::onSearchClick,
            )
        }
    }
}
