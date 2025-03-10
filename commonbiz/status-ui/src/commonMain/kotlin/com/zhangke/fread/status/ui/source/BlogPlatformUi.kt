package com.zhangke.fread.status.ui.source

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.common.resources.logo
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.PlatformSnapshot

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
        protocolLogo = platform.protocol.logo,
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
        protocolLogo = platform.protocol.logo,
    )
}
