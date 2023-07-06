package com.zhangke.utopia.activitypubapp.account.repo

import com.zhangke.utopia.activitypubapp.db.ActivityPubDatabases
import javax.inject.Inject

class ActivityPubAccountRepo @Inject constructor(
    private val databases: ActivityPubDatabases,
) {

    private val userDao: ActivityPubUserDao
        get() = databases.getActivityPubUserDao()

    suspend fun getCurrentUser(): ActivityPubUserEntity? {
        return userDao.queryAll().firstOrNull { it.active }
    }

    suspend fun updateCurrentUser(userEntity: ActivityPubUserEntity) {
        val insertList = mutableListOf<ActivityPubUserEntity>()
        userDao.querySelectedUser()
            ?.copy(active = false)
            ?.let { insertList += it }
        insertList += userEntity
        userDao.insert(insertList)
    }

    suspend fun queryAll(): List<ActivityPubUserEntity> = userDao.queryAll()

    suspend fun queryByUri(uri: String): ActivityPubUserEntity? = userDao.queryByUri(uri)

    suspend fun insert(entry: ActivityPubUserEntity) = userDao.insert(entry)

    suspend fun insert(entries: List<ActivityPubUserEntity>) = userDao.insert(entries)

    suspend fun deleteByUri(uri: String) = userDao.deleteByUri(uri)

    suspend fun clear() = userDao.nukeTable()
}
