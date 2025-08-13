package com.zhangke.fread.rss.internal.adapter

import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.fread.rss.internal.model.RssSource
import com.zhangke.fread.rss.internal.uri.RssUriInsight
import com.zhangke.fread.rss.internal.webfinger.RssSourceWebFingerTransformer
import com.zhangke.fread.status.author.BlogAuthor
import me.tatarka.inject.annotations.Inject

class BlogAuthorAdapter @Inject constructor(
    private val rssSourceWebFingerTransformer: RssSourceWebFingerTransformer,
) {

    fun createAuthor(
        uriInsight: RssUriInsight,
        source: RssSource,
    ): BlogAuthor {
        val webFinger = rssSourceWebFingerTransformer.create(uriInsight.url, source)
        return BlogAuthor(
            uri = uriInsight.rawUri,
            webFinger = webFinger,
            handle = webFinger.toString(),
            name = source.displayName.ifNullOrEmpty { source.title },
            description = source.description.orEmpty(),
            avatar = source.thumbnail,
            banner = null,
            emojis = emptyList(),
            followingCount = null,
            followersCount = null,
            statusesCount = null,
        )
    }
}
