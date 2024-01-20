package com.zhangke.utopia.activitypub.app.internal.repo.account

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubLoggerAccountDao
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.status.uri.FormalUri
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
