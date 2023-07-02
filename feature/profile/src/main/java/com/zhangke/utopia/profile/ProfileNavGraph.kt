package com.zhangke.utopia.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.zhangke.utopia.profile.pages.home.ProfileHomeRoute
import com.zhangke.utopia.profile.pages.home.profileHomeRoute

const val profileModuleRoute = "profile"

fun NavGraphBuilder.profileModuleNavGraph(navController: NavController) {
    navigation(
        startDestination = ProfileHomeRoute().route,
        route = profileModuleRoute
    ) {
        profileHomeRoute(navController)
    }
}
