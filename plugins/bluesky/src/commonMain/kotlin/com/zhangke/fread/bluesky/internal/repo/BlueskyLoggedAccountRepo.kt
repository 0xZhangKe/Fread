package com.zhangke.fread.bluesky.internal.repo

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.db.BlueskyLoggedAccountDao
import com.zhangke.fread.bluesky.internal.db.BlueskyLoggedAccountDatabase
import com.zhangke.fread.bluesky.internal.db.BlueskyLoggedAccountEntity
import com.zhangke.fread.common.utils.getCurrentTimeMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

class BlueskyLoggedAccountRepo @Inject constructor(
    database: BlueskyLoggedAccountDatabase,
) {

    private val dao: BlueskyLoggedAccountDao = database.getDao()

    fun queryAllFlow(): Flow<List<BlueskyLoggedAccount>> {
        return dao.queryAllFlow().map { list -> list.map { it.account } }
    }

    suspend fun queryByUri(uri: String): BlueskyLoggedAccount? {
        return dao.queryByUri(uri)?.account
    }

    suspend fun queryAll(): List<BlueskyLoggedAccount> {
        return dao.queryAll().map { it.account }
    }

    suspend fun insert(account: BlueskyLoggedAccount) {
        val entity = BlueskyLoggedAccountEntity(
            uri = account.uri.toString(),
            account = account,
            addedTimestamp = getCurrentTimeMillis(),
        )
        dao.insert(entity)
    }

    suspend fun updateAccount(account: BlueskyLoggedAccount, newAccount: BlueskyLoggedAccount){
        if (account.did != newAccount.did) {
            // maybe did will change?
            deleteByUri(account.uri.toString())
        }
        insert(newAccount)
    }

    suspend fun deleteByUri(uri: String) {
        dao.deleteByUri(uri)
    }
}
