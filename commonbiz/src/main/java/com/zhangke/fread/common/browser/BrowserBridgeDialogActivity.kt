package com.zhangke.fread.common.browser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.zhangke.framework.architect.theme.FreadTheme
import com.zhangke.framework.toast.toast
import com.zhangke.framework.utils.extractActivity
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.status.model.IdentityRole
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BrowserBridgeDialogActivity : AppCompatActivity() {

    companion object {

        private const val PARAMS_ROLE = "role"
        private const val PARAMS_URL = "url"

        fun open(context: Context, role: IdentityRole, url: String) {
            val activity = context.extractActivity()
            val intent = Intent(activity ?: context, BrowserBridgeDialogActivity::class.java)
            intent.putExtra(PARAMS_ROLE, role as Parcelable)
            intent.putExtra(PARAMS_URL, url)
            if (activity != null) {
                activity.startActivity(intent)
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

    @Inject
    lateinit var browserInterceptorSet: Set<@JvmSuppressWildcards BrowserInterceptor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        val role = intent.getParcelableExtra<IdentityRole>(PARAMS_ROLE)
        val url = intent.getStringExtra(PARAMS_URL)
        if (role == null || url.isNullOrEmpty()) {
            toast("url is empty")
            finish()
            return
        }

        setContent {
            val dayNightMode by DayNightHelper.dayNightModeFlow.collectAsState()
            FreadTheme(
                darkTheme = dayNightMode.isNight,
            ) {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center,
                ) {
                    Surface(
                        modifier = Modifier,
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 24.dp, horizontal = 64.dp)
                                .size(80.dp)
                        )
                    }
                }
            }
        }
        lifecycleScope.launch {
            if (intercept(role, url)) {
                finish()
                return@launch
            }
            BrowserLauncher.launchWebTabInApp(
                context = this@BrowserBridgeDialogActivity,
                url = url,
                checkAppSupportPage = false,
            )
            finish()
        }
    }

    private suspend fun intercept(role: IdentityRole, url: String): Boolean {
        browserInterceptorSet.forEach {
            if (it.intercept(this, role, url)) {
                return true
            }
        }
        return false
    }
}
