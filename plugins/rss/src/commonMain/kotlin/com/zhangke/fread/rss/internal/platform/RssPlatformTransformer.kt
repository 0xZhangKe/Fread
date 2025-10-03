package com.zhangke.fread.rss.internal.platform

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.status.model.createRssProtocol
import com.zhangke.fread.rss.internal.model.RssSource
import com.zhangke.fread.rss.internal.uri.RssUriInsight
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class RssPlatformTransformer @Inject constructor() {

    suspend fun create(
        uriInsight: RssUriInsight,
        source: RssSource,
    ): BlogPlatform {
        return BlogPlatform(
            uri = uriInsight.rawUri.toString(),
            name = source.title,
            description = source.description.orEmpty(),
            baseUrl = FormalBaseUrl.parse(uriInsight.url)!!,
            protocol = createRssProtocol(),
            thumbnail = source.thumbnail,
            supportsQuotePost = false,
        )
    }
}
