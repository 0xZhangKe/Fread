package com.zhangke.fread.rss.internal.webfinger

import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.rss.internal.model.RssSource
import java.net.URL
import javax.inject.Inject

class RssSourceWebFingerTransformer @Inject constructor() {

    fun create(url: String, source: RssSource): WebFinger {
        val name = source.title
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
