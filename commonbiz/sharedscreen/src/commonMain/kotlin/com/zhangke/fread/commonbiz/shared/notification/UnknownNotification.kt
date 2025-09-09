package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.notification.StatusNotification
import org.jetbrains.compose.resources.stringResource

@Composable
fun UnknownNotification(
    notification: StatusNotification.Unknown,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = stringResource(LocalizedString.sharedNotificationUnknownDesc),
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = notification.message,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
