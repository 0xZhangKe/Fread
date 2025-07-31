package com.zhangke.fread.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.NavigationBar
import com.zhangke.framework.composable.NavigationBarItem
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.voyager.AnimatedScreenContentScope
import com.zhangke.fread.common.action.LocalComposableActions
import com.zhangke.fread.common.action.OpenNotificationPageAction
import com.zhangke.fread.common.review.LocalFreadReviewManager
import com.zhangke.fread.common.utils.getCurrentTimeMillis
import com.zhangke.fread.explore.screens.home.ExploreTab
import com.zhangke.fread.feature.message.screens.home.NotificationsTab
import com.zhangke.fread.feeds.pages.home.FeedsHomeTab
import com.zhangke.fread.profile.screen.home.ProfileTab
import com.zhangke.fread.screen.main.drawer.MainDrawer
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NestedTabConnection
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.update.AppUpdateDialog
import com.zhangke.fread.status.ui.utils.getScreenWidth
import com.zhangke.fread.utils.LocalActivityHelper
import kotlinx.coroutines.launch

@OptIn(InternalVoyagerApi::class)
@Composable
fun Screen.MainPage(animatedScreenContentScope: AnimatedScreenContentScope) {
    val activityHelper = LocalActivityHelper.current
    val statusUiConfig = LocalStatusUiConfig.current
    val viewModel = getViewModel<MainViewModel>()
    val tabs = remember { createMainTabs() }
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val nestedTabConnection = remember { NestedTabConnection() }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var inFeedsTab by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState { tabs.size }
    LaunchedEffect(selectedIndex) {
        inFeedsTab = selectedIndex == 0
        pagerState.scrollToPage(selectedIndex)
    }
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
            RegisterNotificationAction(
                tabs = tabs,
                onTabSelected = {
                    val index = tabs.indexOf(it)
                    if (index >= 0 && index < tabs.size && index != selectedIndex) {
                        selectedIndex = index
                    }
                },
            )
            BackHandler(true) {
                if (inFeedsTab) {
                    activityHelper.goHome()
                } else {
                    selectedIndex = 0
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = false,
                    ) {
                        tabs[it].TabContent(
                            screen = this@MainPage,
                            nestedScrollConnection = null,
                            animatedScreenContentScope = animatedScreenContentScope,
                        )
                    }
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
                                selected = index == selectedIndex,
                                detectDoubleTap = inFeedsTab,
                                onDoubleTap = {
                                    coroutineScope.launch {
                                        nestedTabConnection.scrollToTop()
                                    }
                                },
                                onTabSelected = { selectedIndex = index },
                            )
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

private fun createMainTabs(): List<PagerTab> {
    return listOf(
        FeedsHomeTab(),
        ExploreTab(),
        NotificationsTab(),
        ProfileTab(),
    )
}

@Composable
private fun RowScope.TabNavigationItem(
    tab: PagerTab,
    selected: Boolean,
    detectDoubleTap: Boolean,
    onDoubleTap: () -> Unit,
    onTabSelected: (PagerTab) -> Unit,
) {
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
                onTabSelected(tab)
                latestClickTime = 0L
                freadReviewManager.trigger()
            }
        },
        alwaysShowLabel = false,
        icon = { Icon(painter = tab.options!!.icon!!, contentDescription = tab.options?.title) },
    )
}

@Composable
private fun RegisterNotificationAction(
    tabs: List<PagerTab>,
    onTabSelected: (PagerTab) -> Unit,
) {
    val composableActions = LocalComposableActions.current
    LaunchedEffect(tabs, composableActions) {
        composableActions.actionFlow.collect { action ->
            if (!action.startsWith(OpenNotificationPageAction.URI)) return@collect
            val notificationTab =
                tabs.firstNotNullOfOrNull { it as? NotificationsTab } ?: return@collect
            onTabSelected(notificationTab)
        }
    }
}
