package com.zhangke.utopia.explore.pages.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zhangke.utopia.explore.exploreModuleRoute

class ExploreHomeRoute {

    val route = "$exploreModuleRoute/home"
}

internal fun NavGraphBuilder.exploreHomeRoute(navController: NavController) {
    composable(ExploreHomeRoute().route) {
        ExploreHomePage()
    }
}
