package com.zhangke.utopia.activitypubapp.account.repo

import com.zhangke.utopia.activitypubapp.account.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypubapp.account.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubAccountAdapter
import com.zhangke.utopia.activitypubapp.db.ActivityPubDatabases
import javax.inject.Inject

class ActivityPubLoggedAccountRepo @Inject constructor(
    private val databases: ActivityPubDatabases,
    private val adapter: ActivityPubLoggedAccountAdapter,
) {

    private val accountDao: ActivityPubLoggerAccountDao
        get() = databases.getActivityPubUserDao()

    suspend fun getCurrentAccount(): ActivityPubLoggedAccount? {
        return accountDao.queryAll()
            .firstOrNull { it.active }
            ?.let {
                adapter.adapt(it)
            }
    }

    suspend fun updateCurrentAccount(account: ActivityPubLoggedAccount) {
        val insertList = mutableListOf<ActivityPubLoggedAccountEntity>()
        accountDao.querySelectedAccount()
            ?.copy(active = false)
            ?.let { insertList += it }
        insertList += adapter.recovery(account)
        accountDao.insert(insertList)
    }

    suspend fun queryAll(): List<ActivityPubLoggedAccountEntity> = accountDao.queryAll()

    suspend fun queryByUri(uri: String): ActivityPubLoggedAccountEntity? =
        accountDao.queryByUri(uri)

    suspend fun insert(entry: ActivityPubLoggedAccountEntity) = accountDao.insert(entry)

    suspend fun insert(entries: List<ActivityPubLoggedAccountEntity>) = accountDao.insert(entries)

    suspend fun deleteByUri(uri: String) = accountDao.deleteByUri(uri)

    suspend fun clear() = accountDao.nukeTable()
}
