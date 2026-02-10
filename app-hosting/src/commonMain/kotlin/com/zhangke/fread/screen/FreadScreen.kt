package com.zhangke.fread.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.blur.BlurController
import com.zhangke.framework.blur.LocalBlurController
import com.zhangke.framework.composable.BackHandler
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.NavigationBar
import com.zhangke.framework.composable.NavigationBarItem
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.utils.pxToDp
import com.zhangke.fread.common.action.LocalComposableActions
import com.zhangke.fread.common.action.OpenNotificationPageAction
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
import kotlinx.serialization.Serializable

@Serializable
object FreadHomeScreenNavKey : NavKey

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FreadHomeScreenContent(viewModel: MainViewModel) {
    val density = LocalDensity.current
    val activityHelper = LocalActivityHelper.current
    val statusUiConfig = LocalStatusUiConfig.current
    val tabs = remember { createMainTabs() }
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = remember { DrawerState(initialValue = DrawerValue.Closed) }
    val coroutineScope = rememberCoroutineScope()
    val nestedTabConnection = remember { NestedTabConnection() }
    var inFeedsTab by rememberSaveable { mutableStateOf(false) }
    var navigationBarHeight by remember { mutableStateOf(64.dp) }
    ConsumeFlow(nestedTabConnection.openDrawerFlow) {
        if (inFeedsTab) {
            drawerState.open()
        }
    }
    val blurController = remember { BlurController.create() }
    CompositionLocalProvider(
        LocalNestedTabConnection provides nestedTabConnection,
        LocalContentPadding provides PaddingValues(bottom = navigationBarHeight),
        LocalBlurController provides blurController,
        LocalContentColor provides MaterialTheme.colorScheme.onSurface,
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
            val pagerState = rememberPagerState(pageCount = tabs::size)
            BackHandler(true) {
                if (drawerState.isOpen) {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                } else if (inFeedsTab) {
                    activityHelper.goHome()
                } else {
                    pagerState.requestScrollToPage(0)
                }
            }
            LaunchedEffect(pagerState.currentPage) {
                inFeedsTab = pagerState.currentPage == 0
            }
            RegisterNotificationAction(tabs, pagerState)
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                ) { page ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        tabs[page].Content()
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
                        modifier = Modifier.onSizeChanged {
                            navigationBarHeight = it.height.pxToDp(density)
                        },
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            TabNavigationItem(
                                tab = tab,
                                index = index,
                                pagerState = pagerState,
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
        FeedsHomeTab(),
        ExploreTab(),
        NotificationsTab(),
        ProfileTab(),
    )
}

@Composable
private fun RowScope.TabNavigationItem(
    tab: Tab,
    index: Int,
    pagerState: PagerState,
    detectDoubleTap: Boolean,
    onDoubleTap: () -> Unit,
) {
    val selected = pagerState.currentPage == index
    val latestClickTime = remember { mutableLongStateOf(0L) }
    val freadReviewManager = LocalFreadReviewManager.current
    NavigationBarItem(
        selected = selected,
        onClick = {
            if (selected) {
                val currentTime = getCurrentTimeMillis()
                if (detectDoubleTap && currentTime - latestClickTime.value < 500) {
                    onDoubleTap()
                }
                latestClickTime.value = currentTime
                return@NavigationBarItem
            } else {
                pagerState.requestScrollToPage(index)
                latestClickTime.value = 0L
                freadReviewManager.trigger()
            }
        },
        alwaysShowLabel = false,
        icon = {
            Icon(
                painter = tab.options!!.icon!!,
                contentDescription = tab.options!!.title,
            )
        },
    )
}

@Composable
private fun RegisterNotificationAction(
    tabs: List<Tab>,
    pagerState: PagerState,
) {
    val composableActions = LocalComposableActions.current
    LaunchedEffect(tabs, composableActions) {
        composableActions.actionFlow.collect { action ->
            if (handleNotificationAction(action, tabs, pagerState)) {
                composableActions.resetReplayCache()
            }
        }
    }
}

private fun handleNotificationAction(
    action: String,
    tabs: List<Tab>,
    pagerState: PagerState,
): Boolean {
    if (!action.startsWith(OpenNotificationPageAction.URI)) return false
    val notificationTabIndex = tabs.indexOfFirst { it is NotificationsTab }
    if (pagerState.currentPage != notificationTabIndex) {
        pagerState.requestScrollToPage(notificationTabIndex)
    }
    return true
}
