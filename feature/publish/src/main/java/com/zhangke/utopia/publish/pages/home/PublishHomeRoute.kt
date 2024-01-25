package com.zhangke.utopia.publish.pages.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

internal class PublishHomeRoute {

    val route = "publish/home"
}

internal fun NavGraphBuilder.publishHomeRoute() {
    composable(PublishHomeRoute().route) {
        PublishHomePage()
    }
}
