package com.zhangke.fread.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.zhangke.framework.architect.theme.FreadTheme
import com.zhangke.framework.composable.video.ExoPlayerManager
import com.zhangke.framework.composable.video.LocalFullscreenExoPlayerManager
import com.zhangke.framework.composable.video.LocalInlineExoPlayerManager
import com.zhangke.framework.voyager.ROOT_NAVIGATOR_KEY
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.common.daynight.DayNightHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FreadActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        DayNightHelper.setActivityDayNightMode()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val dayNightMode by DayNightHelper.dayNightModeFlow.collectAsState()
            FreadTheme(
                darkTheme = dayNightMode.isNight,
            ) {
                val inlineVideoPlayerManager = remember {
                    ExoPlayerManager()
                }
                val fullScreenPlayerManager = remember {
                    ExoPlayerManager()
                }
                DisposableEffect(inlineVideoPlayerManager, fullScreenPlayerManager) {
                    onDispose {
                        inlineVideoPlayerManager.recycler()
                        fullScreenPlayerManager.recycler()
                    }
                }
                CompositionLocalProvider(
                    LocalInlineExoPlayerManager provides inlineVideoPlayerManager,
                    LocalFullscreenExoPlayerManager provides fullScreenPlayerManager,
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
                                SlideTransition(it)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        recreate()
    }
}
