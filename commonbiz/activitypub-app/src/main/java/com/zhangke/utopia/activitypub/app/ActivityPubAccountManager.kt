package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.account.usecase.GetAllActivityPubLoggedAccountUseCase
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubSourceListAuthValidateUseCase
import com.zhangke.utopia.activitypub.app.internal.auth.LaunchActivityPubAuthUseCase
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.account.SourcesAuthValidateResult
import com.zhangke.utopia.status.platform.BlogPlatform
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

    override fun applicable(platform: BlogPlatform): Boolean {
        return launchAuth.applicable(platform)
    }

    override suspend fun launchAuthBySource(platform: BlogPlatform): Result<Boolean> {
        return launchAuth.launch(platform)
    }
}
