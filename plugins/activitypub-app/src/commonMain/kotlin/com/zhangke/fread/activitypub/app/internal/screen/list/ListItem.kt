package com.zhangke.fread.activitypub.app.internal.screen.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.framework.composable.freadPlaceholder

@Composable
internal fun ListItem(
    modifier: Modifier,
    list: ActivityPubListEntity,
) {
    Row(
        modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ListAlt,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = list.title,
        )
    }
}

@Composable
internal fun ListItemPlaceholder() {
    Row(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .freadPlaceholder(visible = true),
        )
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .width(100.dp)
                .height(16.dp)
                .freadPlaceholder(visible = true),
        )
    }
}
