package com.zhangke.utopia.common.status.repo

import com.zhangke.utopia.common.status.adapter.ContentConfigAdapter
import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContentConfigRepo @Inject constructor(
    private val statusDatabase: StatusDatabase,
    private val contentConfigAdapter: ContentConfigAdapter,
) {

    private val contentConfigDao get() = statusDatabase.getContentConfigDao()

    suspend fun getAllConfig(): List<ContentConfig> {
        return contentConfigDao.queryAllContentConfig().map(contentConfigAdapter::toContentConfig)
    }

    fun getAllConfigFlow(): Flow<List<ContentConfig>> {
        return contentConfigDao.queryAllContentConfigFlow().map {
            it.map(contentConfigAdapter::toContentConfig)
        }
    }

    suspend fun getConfigById(id: Long): ContentConfig? {
        return contentConfigDao.queryById(id)?.let(contentConfigAdapter::toContentConfig)
    }

    suspend fun insert(config: ContentConfig) {
        contentConfigDao.insert(contentConfigAdapter.toEntity(config))
    }

    suspend fun updateSourceList(id: Long, sourceList: List<FormalUri>) {
        contentConfigDao.updateSourceList(id, sourceList)
    }

    suspend fun updateContentName(id: Long, name: String) {
        contentConfigDao.updateName(id, name)
    }

    suspend fun updateLatestStatusId(id: Long, latestStatusId: String) {
        contentConfigDao.updateLatestStatusId(id, latestStatusId)
    }

    suspend fun clearAllLastReadStatusId() {
        contentConfigDao.clearAllLastReadStatusId()
    }

    suspend fun checkNameExist(name: String): Boolean {
        return contentConfigDao.queryByName(name) != null
    }

    suspend fun deleteById(id: Long) {
        contentConfigDao.deleteById(id)
    }

    suspend fun delete(config: ContentConfig) {
        contentConfigDao.delete(contentConfigAdapter.toEntity(config))
    }
}
