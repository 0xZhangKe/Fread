package com.zhangke.fread.rss.internal.rss

import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.rss.internal.model.RssChannelItem
import com.zhangke.fread.rss.internal.model.RssSource
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class RssFetcher @Inject constructor(
    private val rssParserWrapper: RssParserWrapper,
) {

    suspend fun fetchRss(url: String): Result<Pair<RssSource, List<RssChannelItem>>> {
        val channel = try {
            rssParserWrapper.getRssChannel(url)
        } catch (e: Throwable) {
            return Result.failure(e)
        }
        val rssSource = channel.convert(url)
        val rssChannelItem = channel.items.map { it.convert() }
        return Result.success(Pair(rssSource, rssChannelItem))
    }

    private fun RssChannel.convert(url: String): RssSource {
        return RssSource(
            url = url,
            title = this.title,
            homePage = this.link,
            displayName = this.title,
            addDate = this.lastBuildDate ?: Clock.System.now(),
            lastUpdateDate = this.lastBuildDate ?: Clock.System.now(),
            description = this.description,
            updatePeriod = this.updatePeriod,
            thumbnail = this.image?.url,
        )
    }

    private fun RssItem.convert(): RssChannelItem {
        return RssChannelItem(
            id = this.id,
            title = this.title,
            author = this.author,
            link = this.link,
            pubDate = this.pubDate,
            description = this.description,
            content = this.content,
            image = this.image,
            audio = this.audio,
            video = this.video,
            sourceName = this.sourceName,
            sourceUrl = this.sourceUrl,
            categories = this.categories,
            commentsUrl = this.commentsUrl,
        )
    }
}
