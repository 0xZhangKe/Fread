package com.zhangke.fread.profile.screen.setting

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
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.common.handler.LocalActivityTextHandler
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.ic_github_logo
import com.zhangke.fread.feature.profile.ic_telegram
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackBottomSheet(
    onDismissRequest: () -> Unit,
) {
    val textHandler = LocalActivityTextHandler.current
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
                    imageVector = vectorResource(Res.drawable.ic_telegram),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(LocalizedString.profileSettingOpenSourceFeedbackTelegram),
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
                    imageVector = vectorResource(Res.drawable.ic_github_logo),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(LocalizedString.profileSettingOpenSourceFeedbackGithub),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Row(
                modifier = Modifier
                    .clickable {
                        onDismissRequest()
                        textHandler.openSendEmail()
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
                    text = stringResource(LocalizedString.profileSettingOpenSourceFeedbackEmail),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}
