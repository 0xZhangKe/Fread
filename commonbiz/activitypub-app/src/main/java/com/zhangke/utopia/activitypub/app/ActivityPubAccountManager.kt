package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.account.usecase.GetAllActivityPubLoggedAccountUseCase
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubSourceListAuthValidateUseCase
import com.zhangke.utopia.activitypub.app.internal.auth.LaunchActivityPubAuthUseCase
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.account.SourcesAuthValidateResult
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class ActivityPubAccountManager @Inject constructor(
    private val getAllLoggedAccount: GetAllActivityPubLoggedAccountUseCase,
    private val validateAuthOfSourceList: ActivityPubSourceListAuthValidateUseCase,
    private val launchAuth: LaunchActivityPubAuthUseCase,
) : IAccountManager {

    override suspend fun getAllLoggedAccount(): Result<List<LoggedAccount>> {
        return getAllLoggedAccount.invoke()
    }

    override suspend fun validateAuthOfSourceList(
        sourceList: List<StatusSource>,
    ): Result<SourcesAuthValidateResult> {
        return validateAuthOfSourceList.invoke(sourceList)
    }

    override fun applicable(source: StatusSource): Boolean {
        return launchAuth.applicable(source)
    }

    override suspend fun launchAuthBySource(source: StatusSource): Result<Boolean> {
        return launchAuth.launch(source)
    }
}
