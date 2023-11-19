package com.zhangke.utopia.activitypub.app.internal.account.usecase

import com.zhangke.utopia.activitypub.app.internal.account.repo.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.status.account.LoggedAccount
import javax.inject.Inject

class GetAllActivityPubLoggedAccountUseCase @Inject constructor(
    private val accountRepo: ActivityPubLoggedAccountRepo,
) {

    suspend operator fun invoke(): Result<List<LoggedAccount>> {
        return Result.success(accountRepo.queryAll())
    }
}
