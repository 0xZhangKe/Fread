package com.zhangke.fread.status.ui.visibility

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.horizontalPadding
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.R

@Composable
fun StatusVisibilityUi(
    visibility: StatusVisibility,
    style: StatusStyle,
) {
    if (visibility == StatusVisibility.DIRECT) {
        // mentioned only
        IconWithTextLabel(
            icon = Icons.Default.AlternateEmail,
            text = stringResource(R.string.status_ui_visibility_mentioned_only),
            style = style,
        )
    }
}

@Composable
private fun IconWithTextLabel(
    icon: ImageVector,
    text: String,
    style: StatusStyle,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = style.containerPaddings.calculateTopPadding())
            .horizontalPadding(style.containerPaddings),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            maxLines = 1,
            text = text,
            color = MaterialTheme.colorScheme.primary,
            fontSize = style.contentSize.topLabelSize,
        )
    }
}
