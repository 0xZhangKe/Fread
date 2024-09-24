package com.zhangke.fread.rss.internal.uri

import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

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
