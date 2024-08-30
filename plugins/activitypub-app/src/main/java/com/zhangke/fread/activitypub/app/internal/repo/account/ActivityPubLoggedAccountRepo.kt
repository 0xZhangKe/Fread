package com.zhangke.fread.activitypub.app.internal.repo.account

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggerAccountDao
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ActivityPubLoggedAccountRepo @Inject constructor(
    private val databases: ActivityPubDatabases,
    private val adapter: ActivityPubLoggedAccountAdapter,
) {

    private val accountDao: ActivityPubLoggerAccountDao
        get() = databases.getLoggedAccountDao()

    fun getAllAccountFlow(): Flow<List<ActivityPubLoggedAccount>> {
        return accountDao.queryAllFlow().map { list ->
            list.map { adapter.adapt(it) }
        }
    }

    fun observeAccount(baseUrl: FormalBaseUrl): Flow<ActivityPubLoggedAccount?> {
        return accountDao.observeAccount(baseUrl).map {
            it?.let(adapter::adapt)
        }
    }

    suspend fun queryAll(): List<ActivityPubLoggedAccount> =
        accountDao.queryAll().map(adapter::adapt)

    suspend fun queryByUri(uri: String): ActivityPubLoggedAccount? =
        accountDao.queryByUri(uri)?.let(adapter::adapt)

    suspend fun queryByBaseUrl(baseUrl: FormalBaseUrl): ActivityPubLoggedAccount? {
        return accountDao.queryByBaseUrl(baseUrl).firstOrNull()?.let(adapter::adapt)
    }

    suspend fun insert(
        entry: ActivityPubLoggedAccount,
        addedTimestamp: Long,
    ) = accountDao.insert(adapter.recovery(entry, addedTimestamp))

    suspend fun update(account: ActivityPubLoggedAccount){
        val entity = accountDao.queryByUri(account.uri.toString())
        val addedTimestamp = entity?.addedTimestamp ?: System.currentTimeMillis()
        accountDao.insert(adapter.recovery(account, addedTimestamp))
    }

    suspend fun deleteByUri(uri: FormalUri) = accountDao.deleteByUri(uri.toString())

    suspend fun clear() = accountDao.nukeTable()
}
