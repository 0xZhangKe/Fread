package com.zhangke.utopia.pages.sources.maintainer

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation

fun NavGraphBuilder.registerSourceMaintainerNavigation(navController: NavController) {
    navigation(
        route = "source/maintainer",
        startDestination = "source/maintainer/detail"
    ) {
        composable(
            route = "source/maintainer/detail?query={query}",
            arguments = listOf(navArgument("query") { defaultValue = "" })
        ) {
            val viewModel: SourceMaintainerViewModel = hiltViewModel()
            val query = it.arguments?.getString("query").orEmpty()
            LaunchedEffect(it, query) {
                viewModel.prepare(query)
            }
            val uiState = viewModel.uiState.collectAsState().value
            SourceMaintainerPage(
                uiState = uiState,
            )
        }
    }
}
