package com.zhangke.utopia.screen.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.NavigationBar
import com.zhangke.framework.utils.extractActivity
import com.zhangke.utopia.explore.ExploreTab
import com.zhangke.utopia.feature.message.NotificationsTab
import com.zhangke.utopia.feeds.FeedsHomeTab
import com.zhangke.utopia.profile.ProfileTab
import com.zhangke.utopia.screen.main.drawer.MainDrawer
import com.zhangke.utopia.status.ui.common.LocalMainTabConnection
import com.zhangke.utopia.status.ui.common.MainTabConnection
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Screen.MainPage() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val mainTabConnection = remember {
        MainTabConnection()
    }
    var inFeedsTab by remember {
        mutableStateOf(false)
    }
    ConsumeFlow(mainTabConnection.openDrawerFlow) {
        if (inFeedsTab) {
            drawerState.open()
        }
    }
    CompositionLocalProvider(
        LocalMainTabConnection provides mainTabConnection,
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = inFeedsTab,
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
                val tabNavigator = LocalTabNavigator.current
                inFeedsTab = tabNavigator.current.key == tabs.first().key
                tabNavigator.current
                BackHandler {
                    if (inFeedsTab) {
                        goHome(context)
                    } else {
                        tabNavigator.current = tabs.first()
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CurrentTab()
                    }
                    val inImmersiveMode by mainTabConnection.inImmersiveFlow.collectAsState()
                    AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        visible = !inImmersiveMode,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                        ),
                    ) {
                        NavigationBar(
                            modifier = Modifier,
                            height = 80.dp,
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

private fun goHome(context: Context){
    val intent = Intent(Intent.ACTION_MAIN).apply {
        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addCategory(Intent.CATEGORY_HOME)
    }
    context.extractActivity()?.startActivity(intent)
}
