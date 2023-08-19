package com.zhangke.utopia.status.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
internal fun SingleBlogImageLayout(
    style: BlogImageMediaStyle,
    aspect: Float,
    itemContent: @Composable () -> Unit,
) {
    val fixedAspect = style.getCompliantAspect(aspect)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(fixedAspect)
            .clip(RoundedCornerShape(style.radius)),
        contentAlignment = Alignment.Center,
    ) {
        itemContent()
    }
}
