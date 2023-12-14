package com.zhangke.utopia.activitypub.app.internal.source

import com.zhangke.utopia.activitypub.app.internal.uri.TimelineUriTransformer
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class TimelineSourceTransformer @Inject constructor(
    private val timelineUriTransformer: TimelineUriTransformer,
) {

    fun createByPlatform(
        uri: FormalUri,
        platform: BlogPlatform,
    ): StatusSource {
        return StatusSource(
            uri = uri,
            name = platform.name,
            description = platform.description,
            thumbnail = platform.thumbnail,
        )
    }
}
