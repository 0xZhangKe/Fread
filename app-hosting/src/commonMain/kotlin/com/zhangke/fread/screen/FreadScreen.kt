package com.zhangke.fread.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.internal.BackHandler
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.NavigationBar
import com.zhangke.framework.composable.NavigationBarItem
import com.zhangke.fread.common.action.LocalComposableActions
import com.zhangke.fread.common.action.OpenNotificationPageAction
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.common.review.LocalFreadReviewManager
import com.zhangke.fread.common.utils.getCurrentTimeMillis
import com.zhangke.fread.explore.screens.home.ExploreTab
import com.zhangke.fread.feature.message.screens.home.NotificationsTab
import com.zhangke.fread.feeds.pages.home.FeedsHomeTab
import com.zhangke.fread.profile.screen.home.ProfileTab
import com.zhangke.fread.screen.main.MainViewModel
import com.zhangke.fread.screen.main.drawer.MainDrawer
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NestedTabConnection
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.update.AppUpdateDialog
import com.zhangke.fread.status.ui.utils.getScreenWidth
import com.zhangke.fread.utils.LocalActivityHelper
import kotlinx.coroutines.launch

class FreadScreen : BaseScreen() {

    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val activityHelper = LocalActivityHelper.current
        val statusUiConfig = LocalStatusUiConfig.current
        val viewModel = getViewModel<MainViewModel>()
        val tabs = remember { createMainTabs() }
        val uiState by viewModel.uiState.collectAsState()
//        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val drawerState = remember { DrawerState(initialValue = DrawerValue.Closed) }
        val coroutineScope = rememberCoroutineScope()
        val nestedTabConnection = remember { NestedTabConnection() }
        var inFeedsTab by rememberSaveable { mutableStateOf(false) }
        ConsumeFlow(nestedTabConnection.openDrawerFlow) {
            if (inFeedsTab) {
                drawerState.open()
            }
        }
        BackHandler(drawerState.isOpen) {
            coroutineScope.launch {
                drawerState.close()
            }
        }
        CompositionLocalProvider(
            LocalNestedTabConnection provides nestedTabConnection,
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = inFeedsTab,
                drawerContent = {
                    val drawerWidth = getScreenWidth() * 0.8F
                    ModalDrawerSheet(
                        modifier = Modifier.widthIn(max = drawerWidth),
                    ) {
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
                TabNavigator(
                    tab = tabs.first()
                ) {
                    val tabNavigator = LocalTabNavigator.current
                    RegisterNotificationAction(tabs, tabNavigator)
                    inFeedsTab = tabNavigator.current.key == tabs.first().key
                    BackHandler(true) {
                        if (inFeedsTab) {
                            activityHelper.goHome()
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
                        val inImmersiveMode by nestedTabConnection.inImmersiveFlow.collectAsState()
                        AnimatedVisibility(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(),
                            visible = !inFeedsTab || !inImmersiveMode || !statusUiConfig.immersiveNavBar,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                            ),
                            exit = slideOutVertically(
                                targetOffsetY = { it },
                            ),
                        ) {
                            NavigationBar(
                                modifier = Modifier,
                            ) {
                                tabs.forEachIndexed { index, tab ->
                                    TabNavigationItem(
                                        tab = tab,
                                        detectDoubleTap = inFeedsTab,
                                        onDoubleTap = {
                                            coroutineScope.launch {
                                                nestedTabConnection.scrollToTop()
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (uiState.newAppReleaseInfo != null) {
            AppUpdateDialog(
                appReleaseInfo = uiState.newAppReleaseInfo!!,
                onCancel = viewModel::onCancelClick,
                onUpdateClick = viewModel::onUpdateClick,
            )
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
    private fun RowScope.TabNavigationItem(
        tab: Tab,
        detectDoubleTap: Boolean,
        onDoubleTap: () -> Unit,
    ) {
        val tabNavigator = LocalTabNavigator.current
        val selected = tabNavigator.current.key == tab.key
        var latestClickTime by remember { mutableLongStateOf(0L) }
        val freadReviewManager = LocalFreadReviewManager.current
        NavigationBarItem(
            selected = selected,
            onClick = {
                if (selected) {
                    val currentTime = getCurrentTimeMillis()
                    if (detectDoubleTap && currentTime - latestClickTime < 500) {
                        onDoubleTap()
                    }
                    latestClickTime = currentTime
                    return@NavigationBarItem
                } else {
                    tabNavigator.current = tab
                    latestClickTime = 0L
                    freadReviewManager.trigger()
                }
            },
            alwaysShowLabel = false,
            icon = {
                Icon(
                    painter = tab.options.icon!!,
                    contentDescription = tab.options.title,
                )
            },
        )
    }

    @Composable
    private fun RegisterNotificationAction(
        tabs: List<Tab>,
        tabNavigator: TabNavigator,
    ) {
        val composableActions = LocalComposableActions.current
        LaunchedEffect(tabs, composableActions) {
            composableActions.actionFlow.collect { action ->
                if (!action.startsWith(OpenNotificationPageAction.URI)) return@collect
                val notificationTab =
                    tabs.firstNotNullOfOrNull { it as? NotificationsTab } ?: return@collect
                if (tabNavigator.current != notificationTab) {
                    tabNavigator.current = notificationTab
                }
            }
        }
    }
}
