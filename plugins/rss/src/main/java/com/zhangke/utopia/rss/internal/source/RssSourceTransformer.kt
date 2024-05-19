package com.zhangke.utopia.rss.internal.source

import android.content.Context
import com.zhangke.utopia.rss.createRssProtocol
import com.zhangke.utopia.rss.internal.model.RssSource
import com.zhangke.utopia.rss.internal.uri.RssUriInsight
import com.zhangke.utopia.status.source.StatusSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RssSourceTransformer @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun createSource(
        uriInsight: RssUriInsight,
        source: RssSource,
    ): StatusSource {
        return StatusSource(
            uri = uriInsight.rawUri,
            name = source.title,
            description = source.description.orEmpty(),
            thumbnail = source.thumbnail,
            protocol = createRssProtocol(context),
        )
    }
}
