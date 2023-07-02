package com.zhangke.utopia.feeds

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.zhangke.utopia.feeds.pages.home.FeedsHomePageRouter
import com.zhangke.utopia.feeds.pages.home.feedsHomeRoute
import com.zhangke.utopia.feeds.pages.manager.feedsManagerRoute
import com.zhangke.utopia.feeds.pages.manager.search.searchSourceForAddRoute

const val feedsModuleRoute = "feeds"

fun NavGraphBuilder.feedsModuleNavGraph(navController: NavController) {
    navigation(
        startDestination = FeedsHomePageRouter().router,
        route = feedsModuleRoute
    ) {
        feedsHomeRoute(navController)
        feedsManagerRoute(navController)
        searchSourceForAddRoute(navController)
    }
}
