package com.zhangke.fread.status.ui.source

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.common.resources.logo
import com.zhangke.fread.status.source.StatusSource

@Composable
fun StatusSourceUi(
    source: StatusSource,
    modifier: Modifier = Modifier,
) {
    SourceCommonUi(
        modifier = modifier,
        thumbnail = source.thumbnail.orEmpty(),
        title = source.name,
        subtitle = null,
        description = source.description,
        protocolLogo = source.protocol.logo,
    )
}
