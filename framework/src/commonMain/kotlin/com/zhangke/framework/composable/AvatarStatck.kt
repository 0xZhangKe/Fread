package com.zhangke.framework.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage

@Composable
fun AvatarHorizontalStack(
    modifier: Modifier = Modifier,
    avatars: List<String>,
    avatarSize: Dp = 24.dp,
    borderColor: Color = Color.White,
) {
    Box(
        modifier = modifier,
    ) {
        avatars.forEachIndexed { index, imageUrl ->
            val startPadding = (avatarSize * index - avatarSize * 0.2F).coerceAtLeast(0.dp)
            AutoSizeImage(
                imageUrl,
                modifier = Modifier
                    .padding(start = startPadding)
                    .size(avatarSize)
                    .border(color = borderColor, width = 1.dp, shape = CircleShape)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                contentDescription = "avatar",
            )
        }
    }
}
