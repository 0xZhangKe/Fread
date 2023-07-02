package com.zhangke.utopia.pages.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.zhangke.utopia.explore.exploreModuleRoute
import com.zhangke.utopia.feeds.feedsModuleRoute
import com.zhangke.utopia.profile.profileModuleRoute
import com.zhangke.utopia.publish.publishModuleRoute

enum class MainTabItem(
    val route: String,
    val icon: @Composable () -> Unit,
    val label: @Composable (() -> Unit)? = null,
) {

    HOME(
        route = feedsModuleRoute,
        icon = {
            Icon(
                painter = rememberVectorPainter(Icons.Default.Home),
                contentDescription = "HomeTab",
            )
        }
    ),
    EXPLORE(
        route = exploreModuleRoute,
        icon = {
            Icon(
                painter = rememberVectorPainter(Icons.Default.Explore),
                contentDescription = "ExploreTab",
            )
        }
    ),
    PUBLISH(
        route = publishModuleRoute,
        icon = {
            Icon(
                painter = rememberVectorPainter(Icons.Default.Publish),
                contentDescription = "PublishTab",
            )
        }
    ),
    PROFILE(
        route = profileModuleRoute,
        icon = {
            Icon(
                painter = rememberVectorPainter(Icons.Default.Settings),
                contentDescription = "ProfileTab",
            )
        }
    ),
}
