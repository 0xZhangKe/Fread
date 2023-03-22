package com.zhangke.utopia.pages.sources

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.zhangke.utopia.pages.UtopiaRouters
import com.zhangke.utopia.pages.sources.add.AddSourcePage
import com.zhangke.utopia.pages.sources.add.AddSourceViewModel

context (UtopiaRouters)
fun NavGraphBuilder.registerSourcesNavigation() {
    navigation(
        startDestination = Sources.add,
        route = Sources.root,
    ) {
        composable(Sources.detail) {

        }

        composable(Sources.add) {
            val viewModel: AddSourceViewModel = hiltViewModel()
            AddSourcePage(
                uiState = viewModel.uiState.collectAsState().value,
                onSearchClick = viewModel::onSearchClick,
                onAddSourceClick = viewModel::onAddSourceClick,
                onConfirmClick = viewModel::onConfirmClick,
            )
        }
    }
}