package com.zhangke.fread.rss.internal.source

import com.zhangke.fread.rss.createRssProtocol
import com.zhangke.fread.rss.internal.model.RssSource
import com.zhangke.fread.rss.internal.uri.RssUriInsight
import com.zhangke.fread.status.source.StatusSource
import me.tatarka.inject.annotations.Inject

class RssSourceTransformer @Inject constructor() {

    suspend fun createSource(
        uriInsight: RssUriInsight,
        source: RssSource,
    ): StatusSource {
        return StatusSource(
            uri = uriInsight.rawUri,
            name = source.title,
            handle = source.url,
            description = source.description.orEmpty(),
            thumbnail = source.thumbnail,
            protocol = createRssProtocol(),
        )
    }
}
