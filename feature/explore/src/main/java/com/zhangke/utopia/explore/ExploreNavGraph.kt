package com.zhangke.utopia.explore

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.zhangke.utopia.explore.pages.home.ExploreHomeRoute
import com.zhangke.utopia.explore.pages.home.exploreHomeRoute

const val exploreModuleRoute = "explore"

fun NavGraphBuilder.exploreModuleNavGraph(navController: NavController) {
    navigation(
        startDestination = ExploreHomeRoute().route,
        route = exploreModuleRoute,
    ) {
        exploreHomeRoute(navController)
    }
}
