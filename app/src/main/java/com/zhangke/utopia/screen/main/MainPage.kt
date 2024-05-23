package com.zhangke.utopia.screen.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.zhangke.utopia.explore.ExploreTab
import com.zhangke.utopia.feature.message.NotificationsTab
import com.zhangke.utopia.feeds.FeedsHomeTab
import com.zhangke.utopia.feeds.pages.home.HomeToFeedsLinker
import com.zhangke.utopia.feeds.pages.home.LocalHomeToFeedLinker
import com.zhangke.utopia.profile.ProfileTab
import com.zhangke.utopia.screen.main.drawer.MainDrawer
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Screen.MainPage() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val homeToFeedsLinker = remember {
        HomeToFeedsLinker {
            coroutineScope.launch {
                drawerState.open()
            }
        }
    }
    CompositionLocalProvider(
        LocalHomeToFeedLinker provides homeToFeedsLinker
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    MainDrawer(
                        onDismissRequest = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        },
                    )
                }
            },
        ) {
            val tabs = remember {
                createMainTabs()
            }
            TabNavigator(
                tab = tabs.first(),
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
                        tabs.forEach { tab ->
                            TabNavigationItem(tab)
                        }
                    }
                }
            }
        }
    }
}

private fun createMainTabs(): List<Tab> {
    return listOf(
        FeedsHomeTab(0u),
        ExploreTab(1u),
        NotificationsTab(2u),
        ProfileTab(3u),
    )
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
