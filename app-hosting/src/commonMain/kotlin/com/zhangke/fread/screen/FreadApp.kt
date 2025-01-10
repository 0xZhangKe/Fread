package com.zhangke.fread.screen

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.LocalViewModelProviderFactory
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.zhangke.framework.composable.video.LocalVideoPlayerManager
import com.zhangke.framework.composable.video.rememberVideoPlayerManager
import com.zhangke.framework.voyager.ROOT_NAVIGATOR_KEY
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.common.config.LocalFreadConfigManager
import com.zhangke.fread.common.config.LocalLocalConfigManager
import com.zhangke.fread.common.daynight.ActivityDayNightHelper
import com.zhangke.fread.common.daynight.LocalActivityDayNightHelper
import com.zhangke.fread.common.handler.ActivityTextHandler
import com.zhangke.fread.common.handler.LocalActivityTextHandler
import com.zhangke.fread.common.language.ActivityLanguageHelper
import com.zhangke.fread.common.language.LocalActivityLanguageHelper
import com.zhangke.fread.common.review.FreadReviewManager
import com.zhangke.fread.common.review.LocalFreadReviewManager
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.common.utils.LocalMediaFileHelper
import com.zhangke.fread.common.utils.LocalPlatformUriHelper
import com.zhangke.fread.common.utils.LocalThumbnailHelper
import com.zhangke.fread.common.utils.LocalToastHelper
import com.zhangke.fread.common.utils.MediaFileHelper
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.common.utils.ThumbnailHelper
import com.zhangke.fread.common.utils.ToastHelper
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.style.StatusUiConfig
import com.zhangke.fread.utils.ActivityHelper
import com.zhangke.fread.utils.LocalActivityHelper
import me.tatarka.inject.annotations.Inject

typealias FreadApp = @Composable () -> Unit

@OptIn(ExperimentalVoyagerApi::class, ExperimentalMaterialApi::class)
@Inject
@Composable
internal fun FreadApp(
    imageLoader: ImageLoader,
    viewModelProviderFactory: ViewModelProvider.Factory,
    freadReviewManager: FreadReviewManager,
    freadConfigManager: FreadConfigManager,
    localConfigManager: LocalConfigManager,
    platformUriHelper: PlatformUriHelper,
    mediaFileHelper: MediaFileHelper,
    thumbnailHelper: ThumbnailHelper,
    toastHelper: ToastHelper,
    activityDayNightHelper: ActivityDayNightHelper,
    activityLanguageHelper: ActivityLanguageHelper,
    activityTextHandler: ActivityTextHandler,
    activityHelper: ActivityHelper,
) {
    val videoPlayerManager = rememberVideoPlayerManager()
    DisposableEffect(videoPlayerManager) {
        onDispose {
            videoPlayerManager.recycler()
        }
    }
    val statusConfig by freadConfigManager.statusConfigFlow.collectAsState()
    CompositionLocalProvider(
        LocalVideoPlayerManager provides videoPlayerManager,
        LocalStatusUiConfig provides StatusUiConfig.create(config = statusConfig),
        LocalImageLoader provides imageLoader,
        LocalViewModelProviderFactory provides viewModelProviderFactory,
        LocalLocalConfigManager provides localConfigManager,
        LocalFreadConfigManager provides freadConfigManager,
        LocalPlatformUriHelper provides platformUriHelper,
        LocalMediaFileHelper provides mediaFileHelper,
        LocalThumbnailHelper provides thumbnailHelper,
        LocalFreadReviewManager provides freadReviewManager,
        LocalActivityLanguageHelper provides activityLanguageHelper,
        LocalActivityDayNightHelper provides activityDayNightHelper,
        LocalActivityTextHandler provides activityTextHandler,
        LocalToastHelper provides toastHelper,
        LocalActivityHelper provides activityHelper,
    ) {
        ProvideNavigatorLifecycleKMPSupport {
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
                                navigator.push(screen)
                            }
                        }
                    }
                }
            }
        }
    }
}
