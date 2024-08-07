package com.zhangke.fread.status.ui.label

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.horizontalPadding
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.R

@Composable
fun StatusVisibilityLabel(
    visibility: StatusVisibility,
    style: StatusStyle,
) {
    if (visibility == StatusVisibility.DIRECT) {
        // mentioned only
        IconWithTextLabel(
            icon = Icons.Default.AlternateEmail,
            text = stringResource(R.string.status_ui_visibility_mentioned_only),
            style = style,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun StatusPinnedLabel(style: StatusStyle) {
    IconWithTextLabel(
        icon = Icons.Default.PushPin,
        text = stringResource(id = R.string.status_ui_label_pinned),
        style = style,
    )
}

@Composable
private fun IconWithTextLabel(
    icon: ImageVector,
    text: String,
    style: StatusStyle,
    color: Color = LocalContentColor.current,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = style.containerTopPadding)
            .padding(start = style.containerStartPadding, end = style.containerEndPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = icon,
            contentDescription = text,
            tint = color,
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            maxLines = 1,
            text = text,
            color = color,
            fontSize = style.contentSize.topLabelSize,
        )
    }
}
