package com.zhangke.utopia.activitypub.app.internal.usecase.account

import com.zhangke.utopia.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubUserUri
import javax.inject.Inject

class ActiveAccountUseCase @Inject constructor(
    private val accountRepo: ActivityPubLoggedAccountRepo,
) {

    suspend operator fun invoke(uri: ActivityPubUserUri) {
        accountRepo.updateCurrentAccount(uri)
    }
}
