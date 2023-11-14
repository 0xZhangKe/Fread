package com.zhangke.utopia.pages.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.zhangke.utopia.explore.ExploreTab
import com.zhangke.utopia.feeds.FeedsHomeTab
import com.zhangke.utopia.profile.ProfileTab
import com.zhangke.utopia.publish.PublishTab

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainPage() {
    val globalNavigator = LocalNavigator.currentOrThrow
    TabNavigator(tab = FeedsHomeTab) {
        Scaffold(
            bottomBar = {
                BottomNavigation {
                    TabNavigationItem(FeedsHomeTab)
                    TabNavigationItem(ExploreTab)
                    TabNavigationItem(PublishTab)
                    TabNavigationItem(ProfileTab)
                }
            },
            content = {
                CompositionLocalProvider(
                    LocalNavigator provides globalNavigator
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        CurrentTab()
                    }
                }
            }
        )
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) }
    )
}
