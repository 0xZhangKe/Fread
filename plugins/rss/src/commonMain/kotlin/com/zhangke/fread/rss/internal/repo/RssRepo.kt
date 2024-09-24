package com.zhangke.fread.rss.internal.repo

import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.rss.internal.adapter.BlogAuthorAdapter
import com.zhangke.fread.rss.internal.db.RssChannelEntity
import com.zhangke.fread.rss.internal.db.RssDatabases
import com.zhangke.fread.rss.internal.model.RssChannelItem
import com.zhangke.fread.rss.internal.model.RssSource
import com.zhangke.fread.rss.internal.rss.RssFetcher
import com.zhangke.fread.rss.internal.uri.RssUriInsight
import com.zhangke.fread.rss.internal.uri.RssUriTransformer
import com.zhangke.fread.rss.internal.utils.AvatarUtils
import com.zhangke.fread.status.author.BlogAuthor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.tatarka.inject.annotations.Inject
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

@ApplicationScope
class RssRepo @Inject constructor(
    rssDatabases: RssDatabases,
    private val blogAuthorAdapter: BlogAuthorAdapter,
    private val rssUriTransformer: RssUriTransformer,
    private val rssFetcher: RssFetcher,
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
        _sourceChangedFlow.emit(author)
    }

    suspend fun getRssItems(
        url: String,
    ): Result<Pair<RssSource, List<RssChannelItem>>> {
        return fetchRssItemsByUrl(url)
    }

    private suspend fun fetchRssChannelByUrl(url: String): Result<RssSource> {
        return rssFetcher.fetchRss(url)
            .map { mapRemoteSource(url, it.first) to it.second }
            .onSuccess { insertSource(it.first) }
            .map { it.first }
    }

    private suspend fun fetchRssItemsByUrl(
        url: String
    ): Result<Pair<RssSource, List<RssChannelItem>>> {
        return rssFetcher.fetchRss(url)
            .map { mapRemoteSource(url, it.first) to it.second }
            .onSuccess { insertSource(it.first) }
    }

    private suspend fun mapRemoteSource(url: String, source: RssSource): RssSource {
        val localSource = channelDao.queryByUrl(url) ?: return source.copy(thumbnail = processThumbnail(source))
        return source.copy(
            displayName = localSource.displayName,
            addDate = localSource.addDate,
            thumbnail = processThumbnail(localSource.toRssSource()),
        )
    }

    private fun processThumbnail(source: RssSource): String? {
        val thumbnail = source.thumbnail
        if (AvatarUtils.isRemoteAvatar(thumbnail)) return thumbnail
        if (!thumbnail.isNullOrEmpty() && FileSystem.SYSTEM.exists(thumbnail.toPath())) return thumbnail
        return AvatarUtils.makeSourceAvatar(source)
    }

    private suspend fun insertSource(source: RssSource) {
        channelDao.insert(source.toEntity())
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
