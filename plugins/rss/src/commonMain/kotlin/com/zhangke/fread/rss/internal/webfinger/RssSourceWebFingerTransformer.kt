package com.zhangke.fread.rss.internal.webfinger

import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.rss.internal.model.RssSource
import me.tatarka.inject.annotations.Inject

class RssSourceWebFingerTransformer @Inject constructor() {

    fun create(url: String, source: RssSource): WebFinger {
        val name = source.title
        val host = try {
            url.toPlatformUri().host ?: url
        } catch (e: Throwable) {
            url
        }
        return WebFinger.build(
            name = name,
            host = host,
        )
    }
}
