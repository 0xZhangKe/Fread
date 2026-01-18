package com.zhangke.fread.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.LocalSharedTransitionScope
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.common.action.LocalComposableActions
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.bubble.BubbleManager
import com.zhangke.fread.common.bubble.LocalBubbleManager
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.common.config.LocalFreadConfigManager
import com.zhangke.fread.common.config.LocalLocalConfigManager
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.daynight.LocalActivityDayNightHelper
import com.zhangke.fread.common.handler.LocalTextHandler
import com.zhangke.fread.common.handler.TextHandler
import com.zhangke.fread.common.language.ActivityLanguageHelper
import com.zhangke.fread.common.language.LocalActivityLanguageHelper
import com.zhangke.fread.common.review.FreadReviewManager
import com.zhangke.fread.common.review.LocalFreadReviewManager
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.common.utils.LocalMediaFileHelper
import com.zhangke.fread.common.utils.LocalPlatformUriHelper
import com.zhangke.fread.common.utils.LocalToastHelper
import com.zhangke.fread.common.utils.MediaFileHelper
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.common.utils.ToastHelper
import com.zhangke.fread.commonbiz.shared.LocalModuleScreenVisitor
import com.zhangke.fread.commonbiz.shared.ModuleScreenVisitor
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.style.StatusUiConfig
import com.zhangke.fread.utils.ActivityHelper
import com.zhangke.fread.utils.LocalActivityHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.getKoin

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun FreadApp() {
    val koin = getKoin()
    val freadConfigManager: FreadConfigManager = koin.get()
    val statusConfig by freadConfigManager.statusConfigFlow.collectAsState()
    val browserLauncher = koin.get<BrowserLauncher>()
    val imageLoader: ImageLoader = koin.get()
    val freadReviewManager: FreadReviewManager = koin.get()

    val localConfigManager: LocalConfigManager = koin.get()
    val platformUriHelper: PlatformUriHelper = koin.get()
    val mediaFileHelper: MediaFileHelper = koin.get()
    val toastHelper: ToastHelper = koin.get()
    val activityDayNightHelper: DayNightHelper = koin.get()
    val activityLanguageHelper: ActivityLanguageHelper = koin.get()
    val textHandler: TextHandler = koin.get()
    val activityHelper: ActivityHelper = koin.get()
    val moduleScreenVisitor: ModuleScreenVisitor = koin.get()
    val bubbleManager: BubbleManager = koin.get()
    CompositionLocalProvider(
        LocalStatusUiConfig provides StatusUiConfig.create(config = statusConfig),
        LocalImageLoader provides imageLoader,
        LocalLocalConfigManager provides localConfigManager,
        LocalFreadConfigManager provides freadConfigManager,
        LocalPlatformUriHelper provides platformUriHelper,
        LocalMediaFileHelper provides mediaFileHelper,
        LocalFreadReviewManager provides freadReviewManager,
        LocalActivityLanguageHelper provides activityLanguageHelper,
        LocalActivityDayNightHelper provides activityDayNightHelper,
        LocalTextHandler provides textHandler,
        LocalToastHelper provides toastHelper,
        LocalActivityHelper provides activityHelper,
        LocalModuleScreenVisitor provides moduleScreenVisitor,
        LocalBubbleManager provides bubbleManager,
        LocalActivityBrowserLauncher provides browserLauncher,
    ) {
        val navEntryProviders = remember(koin) { koin.getAll<NavEntryProvider>() }
        val backStack = rememberNavBackStack(
            configuration = SavedStateConfiguration {
                serializersModule = SerializersModule {
                    polymorphic(NavKey::class) {
                        for (provider in navEntryProviders) {
                            with(provider) {
                                polymorph()
                            }
                        }
                    }
                }
            },
            FreadHomeScreenNavKey,
        )
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this,
                LocalNavBackStack provides backStack,
            ) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        for (provider in navEntryProviders) {
                            with(provider) { build() }
                        }
                    },
                )
                LaunchedEffect(Unit) {
                    GlobalScreenNavigation.openScreenFlow
                        .debounce(300)
                        .collect { key -> backStack.add(key) }
                }
                LaunchedEffect(Unit) {
                    GlobalScreenNavigation.openTransparentScreenFlow
                        .debounce(300)
                        .collect { backStack.add(it) }
                }
                val browserLauncher = LocalActivityBrowserLauncher.current
                RegisterNotificationAction(browserLauncher)
                val bubbles by bubbleManager.bubbleListFlow.collectAsState()
                if (bubbles.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        for (bubble in bubbles) {
                            with(bubble) { Content() }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RegisterNotificationAction(
    browserLauncher: BrowserLauncher,
) {
    val composableActions = LocalComposableActions.current
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(composableActions) {
        composableActions.actionFlow.collect { action ->
            if (handleHttpUrl(action, browserLauncher, coroutineScope)) {
                composableActions.resetReplayCache()
            }
        }
    }
}

private fun handleHttpUrl(
    action: String,
    browserLauncher: BrowserLauncher,
    coroutineScope: CoroutineScope
): Boolean {
    if (!action.lowercase().startsWith("http")) return false
    coroutineScope.launch {
        browserLauncher.launchWebTabInApp(url = action, isFromExternal = true)
    }
    return true
}
