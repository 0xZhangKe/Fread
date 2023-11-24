package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.account.repo.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.activitypub.app.internal.account.usecase.ActiveAccountUseCase
import com.zhangke.utopia.activitypub.app.internal.account.usecase.GetAllActivityPubLoggedAccountUseCase
import com.zhangke.utopia.activitypub.app.internal.account.usecase.LogoutUseCase
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubSourceListAuthValidateUseCase
import com.zhangke.utopia.activitypub.app.internal.auth.LaunchActivityPubAuthUseCase
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUserUri
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.account.SourcesAuthValidateResult
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.StatusProviderUri
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ActivityPubAccountManager @Inject constructor(
    private val getAllLoggedAccount: GetAllActivityPubLoggedAccountUseCase,
    private val validateAuthOfSourceList: ActivityPubSourceListAuthValidateUseCase,
    private val launchAuth: LaunchActivityPubAuthUseCase,
    private val activeAccount: ActiveAccountUseCase,
    private val logout: LogoutUseCase,
    private val accountRepo: ActivityPubLoggedAccountRepo,
) : IAccountManager {

    override suspend fun getLoggedAccount(): LoggedAccount? {
        return accountRepo.getCurrentAccount()
    }

    override suspend fun getAllLoggedAccount(): Result<List<LoggedAccount>> {
        return getAllLoggedAccount.invoke()
    }

    override fun getAllAccountFlow(): Flow<List<LoggedAccount>> {
        return accountRepo.getAllAccountFlow()
    }

    override suspend fun validateAuthOfSourceList(
        sourceList: List<StatusSource>,
    ): Result<SourcesAuthValidateResult> {
        return validateAuthOfSourceList.invoke(sourceList)
    }

    override suspend fun launchAuthBySource(baseUrl: String): Result<Boolean> {
        return launchAuth.launch(baseUrl)
    }

    override suspend fun activeAccount(uri: StatusProviderUri): Boolean {
        if (uri !is ActivityPubUserUri) return false
        activeAccount.invoke(uri)
        return true
    }

    override suspend fun logout(uri: StatusProviderUri): Boolean {
        if (uri !is ActivityPubUserUri) return false
        logout.invoke(uri)
        return true
    }
}
