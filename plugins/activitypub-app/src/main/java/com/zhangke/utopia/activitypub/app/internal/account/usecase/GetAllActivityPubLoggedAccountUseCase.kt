package com.zhangke.utopia.activitypub.app.internal.account.usecase

import com.zhangke.utopia.activitypub.app.internal.account.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.account.repo.ActivityPubLoggedAccountRepo
import javax.inject.Inject

class GetAllActivityPubLoggedAccountUseCase @Inject constructor(
    private val accountRepo: ActivityPubLoggedAccountRepo,
) {

    suspend operator fun invoke(): List<ActivityPubLoggedAccount> {
        return accountRepo.queryAll()
    }
}
