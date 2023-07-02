package com.zhangke.utopia.publish

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.zhangke.utopia.publish.pages.home.PublishHomeRoute
import com.zhangke.utopia.publish.pages.home.publishHomeRoute

const val publishModuleRoute = "publish"

fun NavGraphBuilder.publishModuleNavGraph(navController: NavController) {
    navigation(
        startDestination = PublishHomeRoute().route,
        route = publishModuleRoute,
    ) {
        publishHomeRoute()
    }
}
