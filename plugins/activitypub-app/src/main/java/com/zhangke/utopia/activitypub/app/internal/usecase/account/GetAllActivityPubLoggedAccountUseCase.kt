package com.zhangke.utopia.activitypub.app.internal.usecase.account

import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import javax.inject.Inject

class GetAllActivityPubLoggedAccountUseCase @Inject constructor(
    private val accountRepo: ActivityPubLoggedAccountRepo,
) {

    suspend operator fun invoke(): List<ActivityPubLoggedAccount> {
        return accountRepo.queryAll()
    }
}
