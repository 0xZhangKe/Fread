package com.zhangke.utopia.activitypub.app

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubOAuthor
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.account.ActiveAccountUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.account.GetAllActivityPubLoggedAccountUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.account.LogoutUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.auth.ActivityPubSourceListAuthValidateUseCase
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.account.SourcesAuthValidateResult
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ActivityPubAccountManager @Inject constructor(
    private val getAllLoggedAccount: GetAllActivityPubLoggedAccountUseCase,
    private val validateAuthOfSourceList: ActivityPubSourceListAuthValidateUseCase,
    private val oAuthor: ActivityPubOAuthor,
    private val activeAccount: ActiveAccountUseCase,
    private val logout: LogoutUseCase,
    private val accountRepo: ActivityPubLoggedAccountRepo,
    private val userUriTransformer: UserUriTransformer,
) : IAccountManager {

    override suspend fun getActiveAccount(): ActivityPubLoggedAccount? {
        return accountRepo.getCurrentAccount()
    }

    override suspend fun getAllLoggedAccount(): List<ActivityPubLoggedAccount> {
        return getAllLoggedAccount.invoke()
    }

    override fun getAllAccountFlow(): Flow<List<ActivityPubLoggedAccount>> {
        return accountRepo.getAllAccountFlow()
    }

    override suspend fun validateAuthOfSourceList(
        sourceList: List<StatusSource>,
    ): Result<SourcesAuthValidateResult> {
        return validateAuthOfSourceList.invoke(sourceList)
    }

    override suspend fun launchAuth(baseUrl: FormalBaseUrl): Result<Boolean> {
        return oAuthor.startOauth(baseUrl)
    }

    override suspend fun activeAccount(uri: FormalUri): Boolean {
        userUriTransformer.parse(uri) ?: return false
        activeAccount.invoke(uri)
        return true
    }

    override suspend fun logout(uri: FormalUri): Boolean {
        userUriTransformer.parse(uri) ?: return false
        logout.invoke(uri)
        return true
    }
}
