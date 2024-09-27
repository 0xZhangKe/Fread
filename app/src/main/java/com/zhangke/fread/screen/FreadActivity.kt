package com.zhangke.fread.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.zhangke.framework.composable.video.ExoPlayerManager
import com.zhangke.framework.composable.video.LocalExoPlayerManager
import com.zhangke.fread.di.AndroidActivityComponent
import com.zhangke.fread.di.component
import com.zhangke.fread.di.create

class FreadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val component = applicationContext.component
        val activityComponent = AndroidActivityComponent.create(component, this)

        val activityDayNightHelper = activityComponent.activityDayNightHelper
        activityDayNightHelper.setDefaultMode()

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            val videoPlayerManager = remember {
                ExoPlayerManager()
            }
            DisposableEffect(videoPlayerManager) {
                onDispose {
                    videoPlayerManager.recycler()
                }
            }
            CompositionLocalProvider(
                LocalExoPlayerManager provides videoPlayerManager,
            ) {
                AndroidFreadApp(
                    activityComponent = activityComponent,
                )
            }
        }
    }
}
