package com.zhangke.utopia.pages.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter

enum class MainTabItem(
    val icon: @Composable () -> Unit,
    val label: @Composable (() -> Unit)? = null,
) {

    HOME(
        icon = {
            Icon(
                painter = rememberVectorPainter(Icons.Default.Home),
                contentDescription = "HomeTab",
            )
        }
    ),
    EXPLORE(
        icon = {
            Icon(
                painter = rememberVectorPainter(Icons.Default.Explore),
                contentDescription = "ExploreTab",
            )
        }
    ),
    PUBLISH(
        icon = {
            Icon(
                painter = rememberVectorPainter(Icons.Default.Publish),
                contentDescription = "PublishTab",
            )
        }
    ),
    PROFILE(
        icon = {
            Icon(
                painter = rememberVectorPainter(Icons.Default.Settings),
                contentDescription = "ProfileTab",
            )
        }
    ),
}
