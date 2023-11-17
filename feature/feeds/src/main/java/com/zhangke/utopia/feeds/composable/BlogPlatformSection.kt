package com.zhangke.utopia.feeds.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.CardInfoSection
import com.zhangke.utopia.status.platform.BlogPlatform

@Composable
fun BlogPlatformSection(
    modifier: Modifier,
    platform: BlogPlatform,
) {
    CardInfoSection(
        modifier = modifier,
        avatar = platform.thumbnail,
        title = platform.name,
        description = platform.description,
    )
}
