package com.zhangke.utopia.pages.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    TabNavigator(
        tab = FeedsHomeTab,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
            ) {
                CurrentTab()
            }
            NavigationBar(
                modifier = Modifier.height(60.dp),
            ) {
                TabNavigationItem(FeedsHomeTab)
                TabNavigationItem(ExploreTab)
                TabNavigationItem(PublishTab)
                TabNavigationItem(ProfileTab)
            }
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    NavigationBarItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        alwaysShowLabel = false,
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
    )
}
