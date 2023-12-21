package com.zhangke.utopia.common.status.repo

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.adapter.FeedsConfigEntityAdapter
import com.zhangke.utopia.common.status.repo.db.FeedsConfigDao
import com.zhangke.utopia.common.status.repo.db.FeedsConfigEntity
import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class FeedsConfigRepo @Inject constructor(
    private val statusDatabase: StatusDatabase,
    private val entityAdapter: FeedsConfigEntityAdapter,
) {

    private val feedsConfigDao: FeedsConfigDao get() = statusDatabase.getFeedsConfigDao()

    suspend fun getAllConfig(): List<FeedsConfig> {
        return feedsConfigDao.queryAllFeedsConfig().map(entityAdapter::toFeedsConfig)
    }

    suspend fun getConfigById(id: Long): FeedsConfig? {
        return feedsConfigDao.queryById(id)?.let(entityAdapter::toFeedsConfig)
    }

    suspend fun checkNameExists(name: String): Boolean {
        return feedsConfigDao.queryByName(name).isNotEmpty()
    }

    suspend fun insert(
        name: String,
        sourceList: List<FormalUri>,
        lastReadStatusId: String? = null,
    ) {
        feedsConfigDao.insertOrReplace(
            FeedsConfigEntity(
                id = 0L,
                name = name,
                sourceUriList = sourceList,
                lastReadStatusId = lastReadStatusId,
            )
        )
    }

    suspend fun updateLastReadStatusId(feedsConfig: FeedsConfig, lastReadStatusId: String?) {
        val newConfig = feedsConfig.copy(lastReadStatusId = lastReadStatusId)
        feedsConfigDao.insertOrReplace(entityAdapter.toEntity(newConfig))
    }

    suspend fun updateSourceList(
        feedsConfigId: Long,
        newSourceList: List<FormalUri>,
    ) {
        feedsConfigDao.updateSourceList(feedsConfigId, newSourceList)
    }

    suspend fun editFeedsName(
        feedsConfigId: Long,
        newName: String
    ) {
        feedsConfigDao.updateFeedsName(feedsConfigId, newName)
    }

    suspend fun insertOrReplace(config: FeedsConfig) {
        feedsConfigDao.insertOrReplace(entityAdapter.toEntity(config))
    }

    suspend fun delete(config: FeedsConfig) {
        feedsConfigDao.delete(entityAdapter.toEntity(config))
    }

    suspend fun deleteById(id: Long) {
        feedsConfigDao.deleteById(id)
    }
}
