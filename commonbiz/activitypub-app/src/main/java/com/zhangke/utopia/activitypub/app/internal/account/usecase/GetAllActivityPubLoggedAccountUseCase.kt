package com.zhangke.utopia.activitypub.app.internal.account.usecase

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypub.app.internal.account.repo.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.status.account.IGetAllAccountUseCase
import com.zhangke.utopia.status.account.LoggedAccount
import javax.inject.Inject

@Filt
class GetAllActivityPubLoggedAccountUseCase @Inject constructor(
    private val accountRepo: ActivityPubLoggedAccountRepo,
) : IGetAllAccountUseCase {

    override suspend fun invoke(): Result<List<LoggedAccount>> {
        return Result.success(accountRepo.queryAll())
    }
}
