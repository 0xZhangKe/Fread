package com.zhangke.utopia.status.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun SingleBlogImageLayout(
    modifier: Modifier,
    style: BlogImageMediaStyle,
    aspect: Float,
    itemContent: @Composable () -> Unit,
) {
    val fixedAspect = style.getCompliantAspect(aspect)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(fixedAspect),
        contentAlignment = Alignment.Center,
    ) {
        itemContent()
    }
}
