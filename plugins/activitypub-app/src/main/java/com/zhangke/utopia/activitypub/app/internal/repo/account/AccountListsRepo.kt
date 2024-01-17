package com.zhangke.utopia.activitypub.app.internal.repo.account

import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.lists.AccountListsDao
import com.zhangke.utopia.activitypub.app.internal.db.lists.AccountListsDatabase
import com.zhangke.utopia.activitypub.app.internal.db.lists.AccountListsEntity
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountListsRepo @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val accountListsDatabase: AccountListsDatabase,
) {

    private val accountListsDao: AccountListsDao get() = accountListsDatabase.getDao()

    suspend fun updateAccountLists(account: ActivityPubLoggedAccount) {
        val accountId = account.userId
        val accountRepo = clientManager.getClient(account.baseUrl).accountRepo
        accountRepo.getAccountLists()
            .onSuccess {
                accountListsDao.insert(AccountListsEntity(accountId, it))
            }
    }

    fun observeAccountLists(accountId: String): Flow<List<ActivityPubListEntity>> {
        return accountListsDao.observeAccountLists(accountId)
            .map { it.firstOrNull()?.lists ?: emptyList() }
    }
}
