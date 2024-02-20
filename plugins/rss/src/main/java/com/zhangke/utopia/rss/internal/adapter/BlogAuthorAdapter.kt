package com.zhangke.utopia.rss.internal.adapter

import com.zhangke.utopia.rss.internal.model.RssSource
import com.zhangke.utopia.rss.internal.uri.RssUriInsight
import com.zhangke.utopia.rss.internal.webfinger.RssSourceWebFingerTransformer
import com.zhangke.utopia.status.author.BlogAuthor
import javax.inject.Inject

class BlogAuthorAdapter @Inject constructor(
    private val rssSourceWebFingerTransformer: RssSourceWebFingerTransformer,
) {

    fun createAuthor(
        uriInsight: RssUriInsight,
        source: RssSource,
    ): BlogAuthor {
        return BlogAuthor(
            uri = uriInsight.rawUri,
            webFinger = rssSourceWebFingerTransformer.create(uriInsight.url, source),
            name = source.title,
            description = source.description.orEmpty(),
            avatar = source.thumbnail,
        )
    }
}
