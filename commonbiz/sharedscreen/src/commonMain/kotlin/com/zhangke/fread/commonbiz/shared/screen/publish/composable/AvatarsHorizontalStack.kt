package com.zhangke.fread.commonbiz.shared.screen.publish.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.ui.BlogAuthorAvatar

@Composable
fun AvatarsHorizontalStack(
    modifier: Modifier,
    avatars: List<String?>,
    style: AvatarStackStyle = AvatarStackStyle.default(),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        avatars.take(3).forEachIndexed { index, url ->
            if (index > 0) {
                Spacer(modifier = Modifier.width(style.avatarSize * 0.3F))
            }
            AvatarWithBorder(
                url = url,
                style = style,
            )
        }
    }
}

@Composable
private fun AvatarWithBorder(url: String?, style: AvatarStackStyle) {
    BlogAuthorAvatar(
        modifier = Modifier.size(style.avatarSize)
            .border(width = 1.dp, color = style.borderColor, shape = CircleShape),
        imageUrl = url,
    )
}

data class AvatarStackStyle(
    val avatarSize: Dp,
    val borderColor: Color,
) {

    companion object {

        @Composable
        fun default(): AvatarStackStyle {
            return AvatarStackStyle(
                avatarSize = 42.dp,
                borderColor = Color.White,
            )
        }
    }
}
