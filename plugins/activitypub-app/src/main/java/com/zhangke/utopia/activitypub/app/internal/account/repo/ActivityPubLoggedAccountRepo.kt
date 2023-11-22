package com.zhangke.utopia.activitypub.app.internal.account.repo

import com.zhangke.utopia.activitypub.app.internal.account.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.account.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUserUri
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

    suspend fun updateCurrentAccount(uri: ActivityPubUserUri) {
        val insertList = mutableListOf<ActivityPubLoggedAccountEntity>()
        accountDao.queryAll()
            .forEach { entity ->
                if (entity.uri == uri.toString()) {
                    insertList += entity.copy(active = true)
                } else if (entity.active) {
                    insertList += entity.copy(active = false)
                }
            }
        accountDao.insert(insertList)
    }

    suspend fun queryAll(): List<ActivityPubLoggedAccount> =
        accountDao.queryAll().map(adapter::adapt)

    suspend fun queryByUri(uri: String): ActivityPubLoggedAccount? =
        accountDao.queryByUri(uri)?.let(adapter::adapt)

    suspend fun insert(entry: ActivityPubLoggedAccount) =
        accountDao.insert(adapter.recovery(entry))

    suspend fun insert(entries: List<ActivityPubLoggedAccount>) =
        accountDao.insert(entries.map(adapter::recovery))

    suspend fun deleteByUri(uri: ActivityPubUserUri) = accountDao.deleteByUri(uri.toString())

    suspend fun clear() = accountDao.nukeTable()
}
