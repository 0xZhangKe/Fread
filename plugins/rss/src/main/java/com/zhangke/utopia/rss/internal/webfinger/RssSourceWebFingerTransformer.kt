package com.zhangke.utopia.rss.internal.webfinger

import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.rss.internal.rss.RssChannel
import com.zhangke.utopia.rss.internal.uri.RssUriInsight
import java.net.URL
import javax.inject.Inject

class RssSourceWebFingerTransformer @Inject constructor() {

    fun create(uriInsight: RssUriInsight, channel: RssChannel): WebFinger {
        val name = channel.title.ifNullOrEmpty { "Unknown" }
        val host = try {
            URL(uriInsight.url).host
        } catch (e: Throwable) {
            uriInsight.url
        }
        return WebFinger.build(
            name = name,
            host = host,
        )
    }
}
