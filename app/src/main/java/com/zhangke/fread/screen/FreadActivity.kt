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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.zhangke.framework.activity.TopActivityManager
import com.zhangke.framework.architect.theme.FreadTheme
import com.zhangke.fread.common.action.ComposableActions
import com.zhangke.fread.common.action.RouteAction
import com.zhangke.fread.common.utils.ActivityResultCallback
import com.zhangke.fread.common.utils.CallbackableActivity
import com.zhangke.fread.di.AndroidActivityComponent
import com.zhangke.fread.di.component
import com.zhangke.fread.di.create
import com.zhangke.krouter.KRouter
import kotlinx.coroutines.launch

class FreadActivity : ComponentActivity(), CallbackableActivity {

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
            FreadTheme(
                darkTheme = dayNightMode.isNight,
            ) {
                activityComponent.freadContent()
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

    override fun registerCallback(requestCode: Int, callback: ActivityResultCallback) {
        callbacks[requestCode] = callback
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        TopActivityManager.updateTopActivity(this)
        callbacks[requestCode]?.invoke(resultCode, data)
    }
}
