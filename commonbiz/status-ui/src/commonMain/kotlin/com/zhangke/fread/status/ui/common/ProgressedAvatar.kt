package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.freadPlaceholder

@Composable
fun ProgressedAvatar(
    modifier: Modifier,
    avatar: String?,
    loading: Boolean,
    progress: Float,
    onAvatarClick: () -> Unit,
) {
    AutoSizeImage(
        url = avatar.orEmpty(),
        modifier = modifier
            .scale(1F - progress)
            .clip(CircleShape)
            .border(2.dp, Color.White, CircleShape)
            .clickable { onAvatarClick() }
            .freadPlaceholder(loading)
            .size(80.dp),
        contentScale = ContentScale.Crop,
        contentDescription = "avatar",
    )
}
