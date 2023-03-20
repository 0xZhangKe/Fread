package com.zhangke.utopia.pages.sources

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.zhangke.utopia.pages.UtopiaRouters
import com.zhangke.utopia.pages.sources.add.AddSourceViewModel

context (UtopiaRouters)
fun NavGraphBuilder.registerSourcesNavigation() {
    navigation(
        startDestination = Sources.search,
        route = Sources.root,
    ) {
        composable(Sources.search) {

        }
        composable(Sources.detail) {

        }

        composable(Sources.add){
            val viewModel: AddSourceViewModel = hiltViewModel()
        }
    }
}