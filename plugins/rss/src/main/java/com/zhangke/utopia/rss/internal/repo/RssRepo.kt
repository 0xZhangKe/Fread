package com.zhangke.utopia.rss.internal.repo

import android.util.Log
import com.zhangke.utopia.rss.internal.adapter.BlogAuthorAdapter
import com.zhangke.utopia.rss.internal.db.RssChannelEntity
import com.zhangke.utopia.rss.internal.db.RssDatabases
import com.zhangke.utopia.rss.internal.model.RssChannelItem
import com.zhangke.utopia.rss.internal.model.RssSource
import com.zhangke.utopia.rss.internal.rss.RssFetcher
import com.zhangke.utopia.rss.internal.uri.RssUriInsight
import com.zhangke.utopia.rss.internal.uri.RssUriTransformer
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RssRepo @Inject constructor(
    rssDatabases: RssDatabases,
    private val blogAuthorAdapter: BlogAuthorAdapter,
    private val rssUriTransformer: RssUriTransformer,
) {

    private val channelDao = rssDatabases.getRssChannelDao()

    private val _sourceChangedFlow = MutableSharedFlow<BlogAuthor>()
    val sourceChangedFlow = _sourceChangedFlow.asSharedFlow()

    suspend fun getRssSource(
        url: String,
        forceRemote: Boolean = false,
    ): Result<RssSource?> {
        if (!forceRemote) {
            channelDao.queryByUrl(url)?.toRssSource()?.let { return Result.success(it) }
        }
        return fetchRssChannelByUrl(url)
    }

    suspend fun updateSourceName(url: String, name: String) {
        val source = channelDao.queryByUrl(url)
        if (source != null) {
            val newSource = source.copy(displayName = name)
            channelDao.insert(newSource)
            updateAuthorFlow(url, newSource)
        }
    }

    private suspend fun updateAuthorFlow(url: String, source: RssChannelEntity) {
        val uri = rssUriTransformer.build(url)
        val uriInsight = RssUriInsight(uri, url)
        val author = blogAuthorAdapter.createAuthor(uriInsight, source.toRssSource())
        Log.d("U_TEST", "RssRepo updateAuthorFlow: ${author.name}")
        _sourceChangedFlow.emit(author)
    }

    suspend fun getRssItems(
        url: String,
    ): Result<Pair<RssSource, List<RssChannelItem>>> {
        return fetchRssItemsByUrl(url)
    }

    private suspend fun fetchRssChannelByUrl(url: String): Result<RssSource> {
        return RssFetcher.fetchRss(url)
            .onSuccess { insertSource(it.first) }
            .map { it.first }
    }

    private suspend fun fetchRssItemsByUrl(
        url: String
    ): Result<Pair<RssSource, List<RssChannelItem>>> {
        return RssFetcher.fetchRss(url)
            .onSuccess { insertSource(it.first) }
    }

    private suspend fun insertSource(source: RssSource) {
        var insertSource = source
        val oldSource = channelDao.queryByUrl(source.url)
        if (oldSource != null) {
            insertSource = source.copy(
                displayName = oldSource.displayName,
                addDate = oldSource.addDate,
            )
        }
        channelDao.insert(insertSource.toEntity())
    }

    private fun RssSource.toEntity(): RssChannelEntity {
        return RssChannelEntity(
            url = this.url,
            homePage = this.homePage,
            title = this.title,
            description = this.description,
            lastBuildDate = this.lastUpdateDate,
            updatePeriod = this.updatePeriod,
            thumbnail = this.thumbnail,
            addDate = this.addDate,
            lastUpdateDate = this.lastUpdateDate,
            displayName = this.displayName,
        )
    }

    private fun RssChannelEntity.toRssSource(): RssSource {
        return RssSource(
            url = this.url,
            homePage = this.homePage,
            title = this.title,
            displayName = this.displayName,
            addDate = this.addDate,
            lastUpdateDate = this.lastUpdateDate,
            description = this.description,
            thumbnail = this.thumbnail,
            updatePeriod = this.updatePeriod,
        )
    }
}
