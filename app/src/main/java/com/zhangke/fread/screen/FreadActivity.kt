package com.zhangke.fread.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.LocalViewModelProviderFactory
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.imageLoader
import com.zhangke.framework.architect.theme.FreadTheme
import com.zhangke.framework.composable.video.ExoPlayerManager
import com.zhangke.framework.composable.video.LocalExoPlayerManager
import com.zhangke.framework.voyager.ROOT_NAVIGATOR_KEY
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.common.action.ComposableActions
import com.zhangke.fread.common.action.RouteAction
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
import com.zhangke.fread.di.ActivityComponent
import com.zhangke.fread.di.component
import com.zhangke.fread.di.create
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.style.StatusUiConfig
import com.zhangke.krouter.KRouter
import kotlinx.coroutines.launch

class FreadActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        if (it) {
            subscribeNotification()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let(::handleIntent)
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalVoyagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val component = applicationContext.component
        val activityComponent = ActivityComponent.create(component, this)

        val activityDayNightHelper = activityComponent.activityDayNightHelper
        activityDayNightHelper.setDefaultMode()

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        initNotification()

        intent?.let(::handleIntent)

        setContent {
            val dayNightMode by activityDayNightHelper.dayNightModeFlow.collectAsState()
            FreadTheme(
                darkTheme = dayNightMode.isNight,
            ) {
                val videoPlayerManager = remember {
                    ExoPlayerManager()
                }
                DisposableEffect(videoPlayerManager) {
                    onDispose {
                        videoPlayerManager.recycler()
                    }
                }
                val statusConfig by component.freadConfigManager.statusConfigFlow.collectAsState()
                CompositionLocalProvider(
                    LocalExoPlayerManager provides videoPlayerManager,
                    LocalStatusUiConfig provides StatusUiConfig.create(config = statusConfig),
                    LocalImageLoader provides applicationContext.imageLoader,
                    LocalViewModelProviderFactory provides component.viewModelProviderFactory,
                    LocalLocalConfigManager provides component.localConfigManager,
                    LocalFreadConfigManager provides component.freadConfigManager,
                    LocalFreadReviewManager provides component.freadReviewManager,
                    LocalActivityLanguageHelper provides activityComponent.activityLanguageHelper,
                    LocalActivityDayNightHelper provides activityComponent.activityDayNightHelper,
                    LocalActivityBrowserLauncher provides activityComponent.activityBrowserLauncher,
                    LocalActivityTextHandler provides activityComponent.activityTextHandler,
                    LocalMediaFileHelper provides component.mediaFileHelper,
                    LocalThumbnailHelper provides component.thumbnailHelper,
                    LocalToastHelper provides activityComponent.toastHelper,
                    LocalPlatformUriHelper provides component.platformUriHelper,
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
        }
    }

    private fun initNotification() {
        if (checkNotificationPermission()) {
            subscribeNotification()
        }
    }

    private fun subscribeNotification() {
        lifecycleScope.launch {
            application.component.statusProvider.accountManager.subscribeNotification()
        }
    }

    private fun checkNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        val selfPermissionState =
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        if (selfPermissionState == PackageManager.PERMISSION_GRANTED) return true
//        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) return false
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        return false
    }

    private fun handleIntent(intent: Intent) {
        val uri = intent.data?.toString() ?: return
        KRouter.route<RouteAction>(uri)?.execute()
        lifecycleScope.launch {
            ComposableActions.post(uri)
        }
    }
}
