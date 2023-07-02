package com.zhangke.utopia.feeds.repo.db

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.feeds.adapter.FeedsEntityAdapter
import com.zhangke.utopia.feeds.model.Feeds
import javax.inject.Inject

internal class FeedsRepo @Inject constructor(
    private val feedAdapter: FeedsEntityAdapter,
) {

    private val databases: FeedsDatabases = FeedsDatabases.createDatabase(appContext)

    private val feedsDao = databases.getDao()

    suspend fun queryAll(): List<Feeds> {
        return feedsDao.queryAll().map { feedAdapter.adapt(it) }
    }

    suspend fun insert(feedsName: String, uriList: List<String>) {
        feedsDao.insert(FeedsEntity(name = feedsName, uriList))
    }

    suspend fun deleteByName(feedName: String) {
        feedsDao.delete(feedName)
    }
}
