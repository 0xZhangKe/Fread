package com.zhangke.utopia.profile.pages.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zhangke.utopia.profile.profileModuleRoute

internal class ProfileHomeRoute {

    val route = "$profileModuleRoute/home"
}

internal fun NavGraphBuilder.profileHomeRoute(navController: NavController) {
    composable(ProfileHomeRoute().route) {
        ProfileHomePage()
    }
}
