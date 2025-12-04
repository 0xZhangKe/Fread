package com.zhangke.fread.common.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImageActionPainter
import com.seiko.imageloader.ui.AutoSizeBox
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.status.account.LoggedAccount

@Composable
internal fun SelectableAccount(
    account: LoggedAccount,
    onClick: (LoggedAccount) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().noRippleClick { onClick(account) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AutoSizeBox(
            request = remember(account.avatar) {
                ImageRequest(account.avatar.orEmpty())
            },
        ) { action ->
            Image(
                painter = rememberImageActionPainter(action),
                contentDescription = "Avatar",
                modifier = Modifier.size(42.dp)
                    .clip(CircleShape)
                    .freadPlaceholder(action !is ImageAction.Success)
                    .clickable { onClick(account) },
            )
        }
        Column(modifier = Modifier.padding(start = 16.dp).weight(1F)) {
            Text(
                text = account.userName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = account.prettyHandle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}