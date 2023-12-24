package com.zhangke.utopia.activitypub.app.internal.repo.account

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubLoggedAccountEntity
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubLoggerAccountDao
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class ActivityPubLoggedAccountRepo @Inject constructor(
    private val databases: ActivityPubDatabases,
    private val adapter: ActivityPubLoggedAccountAdapter,
) {

    private val accountDao: ActivityPubLoggerAccountDao
        get() = databases.getLoggedAccountDao()

    private val baseUrlToAccountCache = ConcurrentHashMap<String, ActivityPubLoggedAccount>()

    init {
        ApplicationScope.launch(Dispatchers.IO) {
            accountDao.queryAllFlow().collect {
                baseUrlToAccountCache.clear()
            }
        }
    }

    fun getAllAccountFlow(): Flow<List<ActivityPubLoggedAccount>> {
        return accountDao.queryAllFlow().map { list ->
            list.map { adapter.adapt(it) }
        }
    }

    suspend fun getUserByBaseUrl(baseUrl: String): ActivityPubLoggedAccount? {
        return baseUrlToAccountCache.getOrPut(baseUrl) {
            val accountList = accountDao.queryByBaseUrl(baseUrl)
            var account = accountList.firstOrNull { it.active }?.let(adapter::adapt)
            if (account == null) {
                account = accountList.firstOrNull()?.let(adapter::adapt)
            }
            if (account == null) return null
            account
        }
    }

    suspend fun getCurrentAccount(): ActivityPubLoggedAccount? {
        return accountDao.queryActiveAccount()?.let(adapter::adapt)
    }

    suspend fun updateCurrentAccount(account: ActivityPubLoggedAccount) {
        val insertList = mutableListOf<ActivityPubLoggedAccountEntity>()
        accountDao.queryActiveAccount()
            ?.copy(active = false)
            ?.let { insertList += it }
        insertList += adapter.recovery(account)
        accountDao.insert(insertList)
    }

    suspend fun updateCurrentAccount(uri: FormalUri) {
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

    suspend fun deleteByUri(uri: FormalUri) = accountDao.deleteByUri(uri.toString())

    suspend fun clear() = accountDao.nukeTable()
}
