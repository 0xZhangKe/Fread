package com.zhangke.utopia.activitypub.app

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubOAuthor
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.auth.ActivityPubSourceListAuthValidateUseCase
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.account.SourcesAuthValidateResult
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ActivityPubAccountManager @Inject constructor(
    private val validateAuthOfSourceList: ActivityPubSourceListAuthValidateUseCase,
    private val oAuthor: ActivityPubOAuthor,
    private val accountRepo: ActivityPubLoggedAccountRepo,
    private val userUriTransformer: UserUriTransformer,
) : IAccountManager {

    override suspend fun getAllLoggedAccount(): List<ActivityPubLoggedAccount> {
        return accountRepo.queryAll()
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

    override suspend fun logout(uri: FormalUri): Boolean {
        userUriTransformer.parse(uri) ?: return false
        accountRepo.deleteByUri(uri)
        return true
    }
}
