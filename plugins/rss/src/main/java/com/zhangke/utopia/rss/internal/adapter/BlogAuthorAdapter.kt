package com.zhangke.utopia.rss.internal.adapter

import com.zhangke.utopia.rss.internal.rss.RssChannel
import com.zhangke.utopia.rss.internal.uri.RssUriInsight
import com.zhangke.utopia.rss.internal.webfinger.RssSourceWebFingerTransformer
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class BlogAuthorAdapter @Inject constructor(
    private val rssSourceWebFingerTransformer: RssSourceWebFingerTransformer,
) {

    fun createAuthor(
        uriInsight: RssUriInsight,
        channel: RssChannel,
    ): BlogAuthor {
        return BlogAuthor(
            uri = uriInsight.rawUri,
            webFinger = rssSourceWebFingerTransformer.create(uriInsight, channel),
            name = channel.title,
            description = channel.description.orEmpty(),
            avatar = channel.image?.url,
        )
    }
}
