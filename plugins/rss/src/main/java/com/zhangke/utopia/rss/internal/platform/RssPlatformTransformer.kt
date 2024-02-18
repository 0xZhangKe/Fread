package com.zhangke.utopia.rss.internal.platform

import android.content.Context
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.rss.getRssProtocol
import com.zhangke.utopia.rss.internal.rss.RssChannel
import com.zhangke.utopia.rss.internal.uri.RssUriTransformer
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.FormalUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RssPlatformTransformer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val rssUriTransformer: RssUriTransformer,
) {

    fun create(
        uri: FormalUri,
        channel: RssChannel,
    ): BlogPlatform {
        return BlogPlatform(
            uri = uri.toString(),
            name = channel.title.ifNullOrEmpty { "Unknown" },
            description = channel.description.orEmpty(),
            baseUrl = FormalBaseUrl.parse(rssUriTransformer.parse(uri)!!.url)!!,
            protocol = getRssProtocol(context),
            thumbnail = channel.image?.url,
        )
    }
}
