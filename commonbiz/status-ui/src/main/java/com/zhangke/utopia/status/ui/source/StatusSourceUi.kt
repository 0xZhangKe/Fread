package com.zhangke.utopia.status.ui.source

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.status.source.StatusSource

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
        protocolName = source.protocol.name,
    )
}
