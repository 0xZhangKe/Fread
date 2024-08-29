package com.zhangke.fread.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
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
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.status.ui.style.LocalStatusStyle
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.style.StatusStyles
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FreadActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterialApi::class, ExperimentalVoyagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        DayNightHelper.setActivityDayNightMode()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            DayNightHelper.dayNightModeFlow.collect {
                recreate()
            }
        }

        setContent {
            FreadTheme(
                darkTheme = DayNightHelper.dayNightMode.isNight,
            ) {
                val videoPlayerManager = remember {
                    ExoPlayerManager()
                }
                DisposableEffect(videoPlayerManager) {
                    onDispose {
                        videoPlayerManager.recycler()
                    }
                }
                val statusContentSize by FreadConfigManager.statusContentSizeFlow.collectAsState()
                CompositionLocalProvider(
                    LocalExoPlayerManager provides videoPlayerManager,
                    LocalStatusStyle provides statusContentSize.toStyle(),
                    LocalImageLoader provides applicationContext.imageLoader,
                ) {
                    TransparentNavigator {
                        BottomSheetNavigator(
                            modifier = Modifier,
                            sheetShape = RoundedCornerShape(12.dp),
                        ) {
                            Navigator(
                                screen = FreadScreen(),
                                key = ROOT_NAVIGATOR_KEY,
                            ) {
                                SlideTransition(
                                    navigator = it,
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

    @Composable
    private fun StatusContentSize.toStyle(): StatusStyle {
        return when (this) {
            StatusContentSize.SMALL -> StatusStyles.small()
            StatusContentSize.MEDIUM -> StatusStyles.medium()
            StatusContentSize.LARGE -> StatusStyles.large()
        }
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        recreate()
    }
}
