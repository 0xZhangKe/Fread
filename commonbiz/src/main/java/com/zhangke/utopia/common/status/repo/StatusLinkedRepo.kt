package com.zhangke.utopia.common.status.repo

import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import com.zhangke.utopia.common.status.repo.db.StatusLinkedEntity
import javax.inject.Inject

class StatusLinkedRepo @Inject constructor(
    private val database: StatusDatabase
) {

    private val statusLinkedDao get() = database.getStatusLinkedDao()

    suspend fun getNextId(id: String): String? {
        return statusLinkedDao.queryById(id)?.nextId
    }

    suspend fun insertList(list: List<Pair<String, String>>) {
        statusLinkedDao.insertOrReplaceList(list.map { StatusLinkedEntity(it.first, it.second) })
    }

    suspend fun insert(id: String, nextId: String) {
        statusLinkedDao.insertOrReplace(StatusLinkedEntity(id = id, nextId = nextId))
    }

    suspend fun deleteById(id: String) {
        statusLinkedDao.deleteById(id)
    }
}
