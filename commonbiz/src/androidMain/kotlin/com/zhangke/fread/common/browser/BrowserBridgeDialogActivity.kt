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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.zhangke.framework.architect.theme.FreadTheme
import com.zhangke.framework.toast.toast
import com.zhangke.framework.utils.appContext
import com.zhangke.framework.utils.extractActivity
import com.zhangke.fread.common.CommonComponent
import com.zhangke.fread.common.commonComponent
import com.zhangke.fread.commonbiz.R
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.launch

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

    private lateinit var commonModule: CommonComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        @Suppress("DEPRECATION")
        val role = intent.getParcelableExtra<IdentityRole>(PARAMS_ROLE)
        val url = intent.getStringExtra(PARAMS_URL)
        if (role == null || url.isNullOrEmpty()) {
            toast("url is empty")
            finish()
            return
        }

        commonModule = appContext.commonComponent

        setContent {
            val dayNightMode by commonModule.dayNightHelper.dayNightModeFlow.collectAsState()
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
            commonModule.browserLauncher.launchWebTabInApp(
                url = url,
                checkAppSupportPage = false,
            )
            finish()
        }
    }

    private suspend fun intercept(role: IdentityRole, url: String): Boolean {
        commonModule.browserInterceptorSet.forEach {
            if (it.intercept(role, url)) {
                return true
            }
        }
        return false
    }

    override fun finish() {
        super.finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
