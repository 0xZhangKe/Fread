package com.zhangke.utopia.status.ui.source

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.platform.PlatformSnapshot

@Composable
fun BlogPlatformUi(
    modifier: Modifier,
    platform: BlogPlatform,
    showDivider: Boolean = true,
) {
    SourceCommonUi(
        modifier = modifier,
        thumbnail = platform.thumbnail.orEmpty(),
        title = platform.name,
        subtitle = platform.baseUrl.host,
        description = platform.description,
        protocolName = platform.protocol.name,
        showDivider = showDivider,
    )
}

@Composable
fun BlogPlatformSnapshotUi(
    modifier: Modifier,
    platform: PlatformSnapshot,
) {
    SourceCommonUi(
        modifier = modifier,
        thumbnail = platform.thumbnail,
        title = platform.domain,
        subtitle = null,
        description = platform.description,
        protocolName = platform.protocol.name,
    )
}
