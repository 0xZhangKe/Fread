package com.zhangke.fread.rss.internal.platform

import android.content.Context
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.rss.createRssProtocol
import com.zhangke.fread.rss.internal.model.RssSource
import com.zhangke.fread.rss.internal.uri.RssUriInsight
import com.zhangke.fread.status.platform.BlogPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RssPlatformTransformer @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun create(
        uriInsight: RssUriInsight,
        source: RssSource,
    ): BlogPlatform {
        return BlogPlatform(
            uri = uriInsight.rawUri.toString(),
            name = source.title,
            description = source.description.orEmpty(),
            baseUrl = FormalBaseUrl.parse(uriInsight.url)!!,
            protocol = createRssProtocol(context),
            thumbnail = source.thumbnail,
        )
    }
}
