package com.zhangke.fread.screen

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.LocalViewModelProviderFactory
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.seiko.imageloader.LocalImageLoader
import com.zhangke.framework.architect.theme.FreadTheme
import com.zhangke.framework.voyager.ROOT_NAVIGATOR_KEY
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.config.LocalFreadConfigManager
import com.zhangke.fread.common.config.LocalLocalConfigManager
import com.zhangke.fread.common.daynight.LocalActivityDayNightHelper
import com.zhangke.fread.common.handler.LocalActivityTextHandler
import com.zhangke.fread.common.language.LocalActivityLanguageHelper
import com.zhangke.fread.common.review.LocalFreadReviewManager
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.common.utils.LocalMediaFileHelper
import com.zhangke.fread.common.utils.LocalPlatformUriHelper
import com.zhangke.fread.common.utils.LocalThumbnailHelper
import com.zhangke.fread.common.utils.LocalToastHelper
import com.zhangke.fread.di.HostingActivityComponent
import com.zhangke.fread.di.HostingApplicationComponent
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.style.StatusUiConfig
import com.zhangke.fread.utils.LocalActivityHelper

@OptIn(ExperimentalVoyagerApi::class, ExperimentalMaterialApi::class)
@Composable
internal fun FreadApp(
    applicationComponent: HostingApplicationComponent,
    activityComponent: HostingActivityComponent,
) {
    val statusConfig by applicationComponent.freadConfigManager.statusConfigFlow.collectAsState()
    CompositionLocalProvider(
        LocalStatusUiConfig provides StatusUiConfig.create(config = statusConfig),
        LocalImageLoader provides applicationComponent.imageLoader,
        LocalViewModelProviderFactory provides applicationComponent.viewModelProviderFactory,
        LocalLocalConfigManager provides applicationComponent.localConfigManager,
        LocalFreadConfigManager provides applicationComponent.freadConfigManager,
        LocalPlatformUriHelper provides applicationComponent.platformUriHelper,
        LocalMediaFileHelper provides applicationComponent.mediaFileHelper,
        LocalThumbnailHelper provides applicationComponent.thumbnailHelper,
        LocalFreadReviewManager provides applicationComponent.freadReviewManager,
        LocalActivityLanguageHelper provides activityComponent.activityLanguageHelper,
        LocalActivityDayNightHelper provides activityComponent.activityDayNightHelper,
        LocalActivityTextHandler provides activityComponent.activityTextHandler,
        LocalToastHelper provides activityComponent.toastHelper,
        LocalActivityHelper provides activityComponent.activityHelper,
    ) {
        ProvideNavigatorLifecycleKMPSupport {
            val dayNightMode by applicationComponent.dayNightHelper.dayNightModeFlow.collectAsState()
            FreadTheme(
                darkTheme = dayNightMode.isNight,
            ) {
                TransparentNavigator {
                    BottomSheetNavigator(
                        modifier = Modifier,
                        sheetShape = RoundedCornerShape(12.dp),
                    ) {
                        Navigator(
                            screen = remember { FreadScreen() },
                            key = ROOT_NAVIGATOR_KEY,
                        ) { navigator ->
                            SlideTransition(
                                navigator = navigator,
                                disposeScreenAfterTransitionEnd = false,
                            )
                            LaunchedEffect(Unit) {
                                GlobalScreenNavigation.openScreenFlow.collect { screen ->
                                    it.push(screen)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}