package com.zhangke.utopia.pages.sources

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.zhangke.utopia.pages.sources.add.addSourceRoute
import com.zhangke.utopia.pages.sources.add.search.searchSourceForAddRoute

fun NavGraphBuilder.registerSourcesNavigation(navController: NavController) {
    val sourceRouter = SourcesRouters()
    navigation(
        startDestination = addSourceRoute,
        route = sourceRouter.root,
    ) {
        searchSourceForAddRoute(navController)
        addSourceRoute(navController)
    }
}