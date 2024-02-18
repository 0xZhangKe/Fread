package com.zhangke.utopia.rss.internal.webfinger

import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.rss.internal.rss.RssChannel
import com.zhangke.utopia.rss.internal.uri.RssUriTransformer
import com.zhangke.utopia.status.uri.FormalUri
import java.net.URL
import javax.inject.Inject

class RssSourceWebFingerTransformer @Inject constructor(
    private val uriTransformer: RssUriTransformer,
) {

    fun create(uri: FormalUri, channel: RssChannel): WebFinger {
        val name = channel.title.ifNullOrEmpty { "Unknown" }
        val url = uriTransformer.parse(uri)!!.url
        val host = try {
            URL(url).host
        } catch (e: Throwable) {
            url
        }
        return WebFinger.build(
            name = name,
            host = host,
        )
    }
}
