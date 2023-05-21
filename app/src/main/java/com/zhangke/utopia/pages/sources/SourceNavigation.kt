package com.zhangke.utopia.pages.sources

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.zhangke.utopia.pages.sources.add.AddSourcePage
import com.zhangke.utopia.pages.sources.add.AddSourceViewModel
import com.zhangke.utopia.pages.sources.search.SearchPage
import com.zhangke.utopia.pages.sources.search.SearchViewModel

fun NavGraphBuilder.registerSourcesNavigation(navController: NavController) {
    val sourceRouter = SourcesRouters()
    navigation(
        startDestination = sourceRouter.add,
        route = sourceRouter.root,
    ) {
        composable(sourceRouter.detail) {

        }

        composable(
            sourceRouter.add,
            arguments = listOf(navArgument("addUri") { defaultValue = "" })
        ) {
            val viewModel: AddSourceViewModel = hiltViewModel()
            val addUri = it.arguments?.getString("addUri")
            if (addUri.isNullOrEmpty().not()) {
                LaunchedEffect(addUri) {
                    viewModel.onAddSource(addUri!!)
                }
            }
            AddSourcePage(
                uiState = viewModel.uiState.collectAsState().value,
                onConfirmClick = viewModel::onConfirmClick,
                onRemoveSourceClick = viewModel::onRemoveSource,
            )
        }

        composable(
            route = sourceRouter.search,
        ) {
            val viewModel: SearchViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsState().value
            SearchPage(
                uiState = uiState,
                onSearchClick = viewModel::onSearchClick,
                onAddSourceClick = {
                    navController.popBackStack(
                        "${sourceRouter.add}?addUri=${it.uri}",
                        inclusive = false,
                    )
                },
            )
        }
    }
}