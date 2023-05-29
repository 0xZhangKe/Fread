package com.zhangke.utopia.db

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.adapter.FeedsEntityAdapter
import com.zhangke.utopia.domain.Feeds
import javax.inject.Inject

class FeedsRepo @Inject constructor(
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
