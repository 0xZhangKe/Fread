package com.zhangke.utopia.pages.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

class MainRouter {

    val route = "main"
}

fun NavGraphBuilder.mainNavigation(navController: NavController) {
    composable(MainRouter().route) {
        MainPage()
    }
}
