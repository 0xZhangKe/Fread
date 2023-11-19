package com.zhangke.utopia.common.feeds.repo

import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.common.feeds.adapter.FeedsEntityAdapter
import com.zhangke.utopia.common.feeds.model.Feeds
import javax.inject.Inject

class FeedsRepo @Inject constructor(
    private val feedAdapter: FeedsEntityAdapter,
) {

    private val databases: FeedsDatabases = FeedsDatabases.createDatabase(appContext)

    private val feedsDao = databases.getDao()

    suspend fun queryAll(): List<Feeds> {
        return feedsDao.queryAll().map { feedAdapter.adapt(it) }
    }

    suspend fun checkNameExists(name: String): Boolean {
        return feedsDao.queryAllNames().contains(name)
    }

    suspend fun queryById(id: Int): Feeds? {
        return feedsDao.queryById(id)?.let(feedAdapter::adapt)
    }

    suspend fun insert(feedsName: String, uriList: List<String>) {
        feedsDao.insert(FeedsEntity(id = 0, name = feedsName, uriList))
    }

    suspend fun update(id: Int, name: String, uriList: List<String>) {
        val entity = FeedsEntity(
            id = id,
            name = name,
            uriList = uriList,
        )
        feedsDao.insert(entity)
    }

    suspend fun deleteById(id: Int) {
        feedsDao.delete(id)
    }
}
