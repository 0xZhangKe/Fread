package com.zhangke.utopia.pages.feeds

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.zhangke.utopia.pages.UtopiaRouters

context (UtopiaRouters)
fun NavGraphBuilder.registerFeedsNavigation() {
    navigation(
        startDestination = Feeds.List,
        route = Feeds.Root
    ) {
        composable(Feeds.List){

        }
    }
}