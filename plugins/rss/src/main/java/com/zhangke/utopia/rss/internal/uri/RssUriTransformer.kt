package com.zhangke.utopia.rss.internal.uri

import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class RssUriTransformer @Inject constructor() {

    companion object {
        private const val PARAM_URL = "url"
    }

    fun build(url: String): FormalUri {
        return createRssUri(
            path = RssUriPath.SOURCE,
            queries = mapOf(PARAM_URL to url),
        )
    }

    fun parse(uri: FormalUri): RssUriInsight? {
        if (!uri.isRssUri) return null
        if (uri.path != RssUriPath.SOURCE) return null
        val sourceUrl = uri.queries[PARAM_URL]
        if (sourceUrl.isNullOrEmpty()) return null
        return RssUriInsight(uri, sourceUrl)
    }
}
