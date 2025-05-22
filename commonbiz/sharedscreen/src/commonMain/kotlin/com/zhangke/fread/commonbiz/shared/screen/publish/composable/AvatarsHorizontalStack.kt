package com.zhangke.fread.commonbiz.shared.screen.publish.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
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
    Box(
        modifier = modifier,
    ) {
        avatars.take(3).forEachIndexed { index, url ->
            AvatarWithBorder(
                modifier = Modifier.padding(start = 8.dp * index),
                url = url,
                style = style,
            )
        }
    }
}

@Composable
private fun AvatarWithBorder(modifier: Modifier, url: String?, style: AvatarStackStyle) {
    BlogAuthorAvatar(
        modifier = modifier.size(style.avatarSize)
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
