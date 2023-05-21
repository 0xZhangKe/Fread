package com.zhangke.utopia.pages.sources.search

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zhangke.utopia.pages.sources.SourcesRouters

fun NavGraphBuilder.registerSearchPageNavigation(navController: NavController) {
    val sourcesRouters = SourcesRouters()
    composable(
        route = sourcesRouters.search,
    ) {
        val viewModel: SearchViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsState().value
        SearchPage(
            uiState = uiState,
            onSearchClick = viewModel::onSearchClick,
            onAddSourceClick = {
                navController.popBackStack(
                    "${sourcesRouters.add}?addUri=${it.uri}",
                    inclusive = false,
                )
            },
        )
    }
}
