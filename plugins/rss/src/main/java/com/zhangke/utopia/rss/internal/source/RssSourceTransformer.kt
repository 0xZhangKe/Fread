package com.zhangke.utopia.rss.internal.source

import androidx.core.net.toUri
import com.zhangke.utopia.rss.internal.rss.RssChannel
import com.zhangke.utopia.rss.internal.uri.RssUriTransformer
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class RssSourceTransformer @Inject constructor(
    private val rssUriTransformer: RssUriTransformer,
) {

    fun createSource(
        url: String,
        channel: RssChannel,
    ): StatusSource {
        val uri = rssUriTransformer.build(url)
        return StatusSource(
            uri = uri,
            name = getName(url, channel),
            description = channel.description.orEmpty(),
            thumbnail = channel.image?.url,
        )
    }

    private fun getName(
        url: String,
        channel: RssChannel,
    ): String {
        channel.title?.let { return it }
        url.toUri().host?.let { return it }
        return url
            .removePrefix("https://").removePrefix("http://")
            .take(6)
    }
}
