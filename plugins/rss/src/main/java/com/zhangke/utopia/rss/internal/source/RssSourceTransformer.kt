package com.zhangke.utopia.rss.internal.source

import com.zhangke.utopia.rss.internal.rss.RssChannel
import com.zhangke.utopia.rss.internal.uri.RssUriInsight
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class RssSourceTransformer @Inject constructor() {

    fun createSource(
        uriInsight: RssUriInsight,
        channel: RssChannel,
    ): StatusSource {
        return StatusSource(
            uri = uriInsight.rawUri,
            name = channel.title,
            description = channel.description.orEmpty(),
            thumbnail = channel.image?.url,
        )
    }
}
