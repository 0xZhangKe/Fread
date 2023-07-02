package com.zhangke.utopia.publish.pages.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zhangke.utopia.publish.publishModuleRoute

internal class PublishHomeRoute {

    val route = "$publishModuleRoute/home"
}

internal fun NavGraphBuilder.publishHomeRoute() {
    composable(PublishHomeRoute().route) {
        PublishHomePage()
    }
}
