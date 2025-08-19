package com.zhangke.fread.activitypub.app.internal.repo.account

import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountDao
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountDatabase
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountEntity
import com.zhangke.fread.activitypub.app.internal.db.old.OldActivityPubLoggerAccountDao
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.utils.getCurrentTimeMillis
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class ActivityPubLoggedAccountRepo @Inject constructor(
    private val oldDatabases: ActivityPubDatabases,
    private val accountDatabase: ActivityPubLoggedAccountDatabase,
) {

    private val _onNewAccountFlow = MutableSharedFlow<ActivityPubLoggedAccount>()
    val onNewAccountFlow: Flow<ActivityPubLoggedAccount> = _onNewAccountFlow

    private val oldAccountDao: OldActivityPubLoggerAccountDao
        get() = oldDatabases.getLoggedAccountDao()

    private val accountDao: ActivityPubLoggedAccountDao
        get() = accountDatabase.getDao()

    suspend fun initialize() {
        LoggedAccountMigrateUtil.migrate(
            oldDao = oldAccountDao,
            accountDao = accountDao,
        )
    }

    fun getAllAccountFlow(): Flow<List<ActivityPubLoggedAccount>> {
        return accountDao.queryAllFlow().map { list ->
            list.map { it.account }
        }
    }

    fun observeAccount(uri: String): Flow<ActivityPubLoggedAccount?> {
        return accountDao.observeAccount(uri)
            .map { it?.account }
    }

    suspend fun queryAll(): List<ActivityPubLoggedAccount> =
        accountDao.queryAll().map { it.account }

    suspend fun queryByUri(uri: String): ActivityPubLoggedAccount? =
        accountDao.queryByUri(uri)?.account

    suspend fun insert(
        account: ActivityPubLoggedAccount,
        addedTimestamp: Long,
    ) {
        val entity = buildAccountEntity(account, addedTimestamp)
        accountDao.insert(entity)
        _onNewAccountFlow.emit(account)
    }

    suspend fun update(account: ActivityPubLoggedAccount) {
        val addedTimestamp =
            accountDao.queryByUri(account.uri.toString())?.addedTimestamp ?: getCurrentTimeMillis()
        val entity = buildAccountEntity(account, addedTimestamp)
        accountDao.insert(entity)
    }

    private fun buildAccountEntity(
        account: ActivityPubLoggedAccount,
        addedTimestamp: Long,
    ): ActivityPubLoggedAccountEntity {
        return ActivityPubLoggedAccountEntity(
            uri = account.uri.toString(),
            account = account,
            addedTimestamp = addedTimestamp,
        )
    }

    suspend fun deleteByUri(uri: FormalUri) {
        accountDao.deleteByUri(uri.toString())
        oldAccountDao.deleteByUri(uri.toString())
    }

    suspend fun clear() = accountDao.nukeTable()
}
