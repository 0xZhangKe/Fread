package com.zhangke.fread.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.zhangke.framework.activity.TopActivityManager
import com.zhangke.framework.architect.theme.FreadTheme
import com.zhangke.framework.composable.video.ExoPlayerManager
import com.zhangke.framework.composable.video.LocalExoPlayerManager
import com.zhangke.fread.common.action.ComposableActions
import com.zhangke.fread.common.action.RouteAction
import com.zhangke.fread.common.utils.ActivityResultCallback
import com.zhangke.fread.common.utils.CallbackableActivity
import com.zhangke.fread.di.AndroidActivityComponent
import com.zhangke.fread.di.component
import com.zhangke.fread.di.create
import com.zhangke.krouter.KRouter
import kotlinx.coroutines.launch

class FreadActivity : AppCompatActivity(), CallbackableActivity {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        if (it) {
            subscribeNotification()
        }
    }

    private val callbacks = mutableMapOf<Int, ActivityResultCallback>()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val component = applicationContext.component
        val activityComponent = AndroidActivityComponent.create(component, this)

        val activityDayNightHelper = activityComponent.activityDayNightHelper
        activityDayNightHelper.setDefaultMode()

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        initNotification()

        intent?.let(::handleIntent)

        setContent {
            val dayNightMode by activityDayNightHelper.dayNightModeFlow.collectAsState()
            val darkTheme = dayNightMode.isNight
            FreadTheme(
                darkTheme = darkTheme,
                dynamicColors = getDynamicColorScheme(darkTheme),
            ) {
                val videoPlayerManager = remember { ExoPlayerManager() }
                DisposableEffect(videoPlayerManager) {
                    onDispose {
                        videoPlayerManager.recycler()
                    }
                }
                CompositionLocalProvider(
                    LocalExoPlayerManager provides videoPlayerManager,
                ) {
                    activityComponent.freadContent()
                }
            }
        }
    }

    private fun getDynamicColorScheme(dark: Boolean): ColorScheme? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (dark) {
                dynamicDarkColorScheme(this)
            } else {
                dynamicLightColorScheme(this)
            }
        } else {
            null
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

    override fun registerCallback(requestCode: Int, callback: ActivityResultCallback) {
        callbacks[requestCode] = callback
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        TopActivityManager.updateTopActivity(this)
        callbacks[requestCode]?.invoke(resultCode, data)
    }
}
