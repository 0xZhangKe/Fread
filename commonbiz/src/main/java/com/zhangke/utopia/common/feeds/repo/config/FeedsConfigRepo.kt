package com.zhangke.utopia.common.feeds.repo.config

import com.zhangke.utopia.common.feeds.adapter.FeedsConfigEntityAdapter
import com.zhangke.utopia.common.feeds.model.FeedsConfig
import javax.inject.Inject

class FeedsConfigRepo @Inject constructor(
    private val feedsConfigDataBase: FeedsConfigDatabase,
    private val entityAdapter: FeedsConfigEntityAdapter,
) {

    private val feedsConfigDao: FeedsConfigDao get() = feedsConfigDataBase.getDao()

    suspend fun getAllConfig(): List<FeedsConfig> {
        return feedsConfigDao.queryAllFeedsConfig().map(entityAdapter::toFeedsConfig)
    }

    suspend fun getConfigByUserId(userId: String): List<FeedsConfig> {
        return feedsConfigDao.queryFeedsConfigByUserId(userId).map(entityAdapter::toFeedsConfig)
    }

    suspend fun insertOrReplace(config: FeedsConfig) {
        feedsConfigDao.insertOrReplace(entityAdapter.toEntity(config))
    }

    suspend fun delete(config: FeedsConfig) {
        feedsConfigDao.delete(entityAdapter.toEntity(config))
    }
}
