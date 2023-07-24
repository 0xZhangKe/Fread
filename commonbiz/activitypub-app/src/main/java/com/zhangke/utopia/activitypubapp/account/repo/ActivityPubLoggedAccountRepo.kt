package com.zhangke.utopia.activitypubapp.account.repo

import com.zhangke.utopia.activitypubapp.db.ActivityPubDatabases
import javax.inject.Inject

class ActivityPubLoggedAccountRepo @Inject constructor(
    private val databases: ActivityPubDatabases,
) {

    private val userDao: ActivityPubLoggerAccountDao
        get() = databases.getActivityPubUserDao()

    suspend fun getCurrentUser(): ActivityPubLoggedAccountEntity? {
        return userDao.queryAll().firstOrNull { it.active }
    }

    suspend fun updateCurrentUser(userEntity: ActivityPubLoggedAccountEntity) {
        val insertList = mutableListOf<ActivityPubLoggedAccountEntity>()
        userDao.querySelectedAccount()
            ?.copy(active = false)
            ?.let { insertList += it }
        insertList += userEntity
        userDao.insert(insertList)
    }

    suspend fun queryAll(): List<ActivityPubLoggedAccountEntity> = userDao.queryAll()

    suspend fun queryByUri(uri: String): ActivityPubLoggedAccountEntity? = userDao.queryByUri(uri)

    suspend fun insert(entry: ActivityPubLoggedAccountEntity) = userDao.insert(entry)

    suspend fun insert(entries: List<ActivityPubLoggedAccountEntity>) = userDao.insert(entries)

    suspend fun deleteByUri(uri: String) = userDao.deleteByUri(uri)

    suspend fun clear() = userDao.nukeTable()
}
