package com.zhangke.fread.profile.screen.setting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.SystemUtils
import com.zhangke.framework.utils.extractActivity
import com.zhangke.fread.analytics.report
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.profile.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackBottomSheet(
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val browserLauncher = LocalActivityBrowserLauncher.current
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
                onDismissRequest()
            }
        },
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable {
                        onDismissRequest()
                        browserLauncher.launchWebTabInApp(AppCommonConfig.TELEGRAM_GROUP)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_telegram),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(id = R.string.profile_setting_open_source_feedback_telegram),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Row(
                modifier = Modifier
                    .clickable {
                        onDismissRequest()
                        browserLauncher.launchWebTabInApp(AppCommonConfig.FEEDBACK_URL)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_github_logo),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(id = R.string.profile_setting_open_source_feedback_github),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Row(
                modifier = Modifier
                    .clickable {
                        onDismissRequest()
                        openSendEmail(context)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(id = R.string.profile_setting_open_source_feedback_email),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

private fun openSendEmail(context: Context) {
    SystemUtils.copyText(context, AppCommonConfig.AUTHOR_EMAIL)
    val intent = Intent(Intent.ACTION_SEND)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, AppCommonConfig.AUTHOR_EMAIL)
    context.startActivityAsActivity(intent)
}

@SuppressLint("QueryPermissionsNeeded")
private fun Context.startActivityAsActivity(intent: Intent) {
    val activity = this.extractActivity()
    if (activity != null) {
        if (intent.resolveActivity(packageManager) != null) {
            activity.startActivity(intent)
        }
    } else {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent.resolveActivity(packageManager) != null) {
                this.startActivity(intent)
            }
        } catch (e: Throwable) {
            report("StartActivityCrash") {
                putString("message", e.message)
                putString("stackTrace", e.stackTraceToString())
            }
        }
    }
}
