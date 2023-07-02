package com.zhangke.utopia.pages.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.zhangke.utopia.explore.exploreModuleNavGraph
import com.zhangke.utopia.feeds.feedsModuleNavGraph
import com.zhangke.utopia.feeds.feedsModuleRoute
import com.zhangke.utopia.profile.profileModuleNavGraph
import com.zhangke.utopia.publish.publishModuleNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage() {
    val navController = rememberNavController()
    val tabs = remember {
        listOf(MainTabItem.HOME, MainTabItem.EXPLORE, MainTabItem.PUBLISH, MainTabItem.PROFILE)
    }
    var selectedTabItem by remember {
        mutableStateOf(MainTabItem.HOME)
    }
    Scaffold(
        bottomBar = {
            MainBottomBar(
                selectedTabItem = selectedTabItem,
                tabs = tabs,
                onTabClick = {
                    selectedTabItem = it
                    navController.navigate(it.route)
                }
            )
        }
    ) { paddings ->
        NavHost(
            navController = navController,
            startDestination = feedsModuleRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
        ) {
            feedsModuleNavGraph(navController)
            exploreModuleNavGraph(navController)
            publishModuleNavGraph(navController)
            profileModuleNavGraph(navController)
        }
    }
}

@Composable
private fun MainBottomBar(
    modifier: Modifier = Modifier,
    selectedTabItem: MainTabItem,
    tabs: List<MainTabItem>,
    onTabClick: (MainTabItem) -> Unit,
) {
    BottomNavigation(
        modifier = modifier.fillMaxWidth()
    ) {
        tabs.forEach { tab ->
            BottomNavigationItem(
                selected = tab == selectedTabItem,
                onClick = { onTabClick(tab) },
                icon = tab.icon,
                label = tab.label,
            )
        }
    }
}
