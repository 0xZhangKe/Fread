package com.zhangke.utopia.common.status.repo

import com.zhangke.utopia.common.status.adapter.StatusSourceEntityAdapter
import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import com.zhangke.utopia.common.status.repo.db.StatusSourceDao
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class StatusSourceRepo @Inject constructor(
    private val statusDatabase: StatusDatabase,
    private val sourceEntityAdapter: StatusSourceEntityAdapter,
){

    private val statusSourceDao: StatusSourceDao get() = statusDatabase.getSourceDao()

    suspend fun queryAllSource(): List<StatusSource>{
        return statusSourceDao.queryAllSource().map(sourceEntityAdapter::toSource)
    }

    suspend fun queryByUri(uri: StatusProviderUri): StatusSource?{
        return statusSourceDao.queryByUri(uri)?.let(sourceEntityAdapter::toSource)
    }

    suspend fun insertOrReplace(source: StatusSource){
        statusSourceDao.insertOrReplace(sourceEntityAdapter.toEntity(source))
    }

    suspend fun delete(source: StatusSource){
        statusSourceDao.delete(sourceEntityAdapter.toEntity(source))
    }
}
