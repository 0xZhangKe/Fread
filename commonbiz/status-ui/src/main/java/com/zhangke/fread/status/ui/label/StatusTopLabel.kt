package com.zhangke.fread.status.ui.label

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.R

@Composable
fun StatusMentionOnlyLabel(
    modifier: Modifier,
    style: StatusStyle,
) {
    IconWithTextLabel(
        modifier = modifier,
        icon = Icons.Default.AlternateEmail,
        text = stringResource(R.string.status_ui_visibility_mentioned_only),
        style = style,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun ReblogTopLabel(
    author: BlogAuthor,
    style: StatusStyle,
    onAuthorClick: (BlogAuthor) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = style.containerTopPadding)
            .padding(start = style.containerStartPadding, end = style.containerEndPadding)
            .noRippleClick { onAuthorClick(author) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(style.topLabelStyle.iconSize),
            imageVector = ImageVector.vectorResource(R.drawable.ic_status_forward),
            contentDescription = null,
            tint = style.secondaryFontColor,
        )
        FreadRichText(
            modifier = Modifier.padding(start = 6.dp),
            richText = author.humanizedName,
            maxLines = 1,
            onHashtagClick = {},
            onMentionClick = {},
            onUrlClick = {},
            fontSizeSp = style.topLabelStyle.textSize.value,
            color = style.secondaryFontColor,
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = stringResource(R.string.status_ui_forward),
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            fontSize = style.topLabelStyle.textSize,
            color = style.secondaryFontColor,
        )
    }
}

@Composable
fun StatusPinnedLabel(
    modifier: Modifier = Modifier,
    style: StatusStyle,
) {
    IconWithTextLabel(
        modifier = modifier,
        icon = Icons.Default.PushPin,
        text = stringResource(id = R.string.status_ui_label_pinned),
        style = style,
    )
}

@Composable
private fun IconWithTextLabel(
    modifier: Modifier,
    icon: ImageVector,
    text: String,
    style: StatusStyle,
    color: Color = style.secondaryFontColor,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = style.containerStartPadding,
                top = style.containerTopPadding / 2,
                end = style.containerEndPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(style.topLabelStyle.iconSize),
            imageVector = icon,
            contentDescription = text,
            tint = color,
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            maxLines = 1,
            text = text,
            color = color,
            fontSize = style.topLabelStyle.textSize,
        )
    }
}
