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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.ic_status_forward
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun StatusMentionOnlyLabel(
    modifier: Modifier,
    style: StatusStyle,
) {
    IconWithTextLabel(
        modifier = modifier,
        icon = Icons.Default.AlternateEmail,
        text = stringResource(LocalizedString.statusUiVisibilityMentionedOnly),
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
    val startPadding =
        style.containerStartPadding + style.infoLineStyle.avatarSize - style.topLabelStyle.iconSize
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = startPadding,
                end = style.containerEndPadding,
            )
            .noRippleClick { onAuthorClick(author) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(style.topLabelStyle.iconSize),
            imageVector = vectorResource(Res.drawable.ic_status_forward),
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
            text = stringResource(LocalizedString.statusUiBoosted),
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
        text = stringResource(LocalizedString.statusUiLabelPinned),
        style = style,
    )
}

@Composable
fun ContinueThread(
    style: StatusStyle,
    onHeightChanged: (Int) -> Unit,
) {
    val startPadding =
        style.containerStartPadding + style.infoLineStyle.avatarSize + style.infoLineStyle.nameToAvatarSpacing
    Text(
        modifier = Modifier.padding(
            start = startPadding,
            end = style.containerEndPadding,
        ).onSizeChanged { onHeightChanged(it.height) },
        maxLines = 1,
        text = stringResource(LocalizedString.statusUiTopLabelContinuedThread),
        color = style.secondaryFontColor,
        fontSize = style.topLabelStyle.textSize,
        textDecoration = TextDecoration.Underline,
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
    val startPadding =
        style.containerStartPadding + style.infoLineStyle.avatarSize - style.topLabelStyle.iconSize
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = startPadding,
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
            modifier = Modifier.padding(start = style.infoLineStyle.nameToAvatarSpacing),
            maxLines = 1,
            text = text,
            color = color,
            fontSize = style.topLabelStyle.textSize,
        )
    }
}
